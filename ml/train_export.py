from __future__ import annotations

import csv
import hashlib
import json
from dataclasses import dataclass
from pathlib import Path

import numpy as np
from sklearn.linear_model import Perceptron
from sklearn.metrics import accuracy_score, confusion_matrix, f1_score, precision_score, recall_score
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler


FEATURE_ORDER = ["age", "systolic_bp", "total_cholesterol", "smoking"]
LABEL_TO_CLASS = {"Risiko Rendah": 0, "Risiko Tinggi": 1}
ROOT = Path(__file__).resolve().parents[1]
DATASET_PATH = ROOT / "ml" / "data" / "dataset.csv"
ARTIFACT_PATH = ROOT / "app" / "src" / "main" / "assets" / "risk_model.json"
PARITY_PATH = ROOT / "app" / "src" / "test" / "resources" / "parity_cases.json"
METRICS_PATH = ROOT / "ml" / "artifacts" / "metrics.json"
REPORT_PATH = ROOT / "docs" / "model_evaluation.md"


@dataclass(frozen=True)
class TrainingOutput:
    artifact: dict
    parity_fixture: dict
    metrics: dict


def load_dataset(path: Path = DATASET_PATH) -> tuple[np.ndarray, np.ndarray, list[dict[str, str]]]:
    with path.open("r", encoding="utf-8-sig", newline="") as source:
        rows = list(csv.DictReader(source))

    if not rows:
        raise ValueError("Dataset kosong.")

    missing_columns = set(FEATURE_ORDER + ["risk_label", "linear_risk_score"]) - set(rows[0])
    if missing_columns:
        raise ValueError(f"Kolom dataset tidak lengkap: {sorted(missing_columns)}")

    features = np.array(
        [[float(row[column]) for column in FEATURE_ORDER] for row in rows],
        dtype=np.float64,
    )
    try:
        labels = np.array([LABEL_TO_CLASS[row["risk_label"]] for row in rows], dtype=np.int64)
    except KeyError as error:
        raise ValueError(f"Label tidak dikenal: {error.args[0]}") from error

    expected_labels = np.array(
        [1 if float(row["linear_risk_score"]) >= 0.48 else 0 for row in rows],
        dtype=np.int64,
    )
    if not np.array_equal(labels, expected_labels):
        mismatches = int(np.count_nonzero(labels != expected_labels))
        raise ValueError(f"Ada {mismatches} label yang tidak sesuai threshold 0.48.")

    return features, labels, rows


def canonical_dataset_sha256(path: Path = DATASET_PATH) -> str:
    """Hash CSV text consistently across Git checkouts with LF or CRLF."""
    canonical_bytes = path.read_bytes().replace(b"\r\n", b"\n")
    return hashlib.sha256(canonical_bytes).hexdigest()


def calculate_metrics(expected: np.ndarray, predicted: np.ndarray) -> dict:
    tn, fp, fn, tp = confusion_matrix(expected, predicted, labels=[0, 1]).ravel()
    return {
        "accuracy": float(accuracy_score(expected, predicted)),
        "precision": float(precision_score(expected, predicted, zero_division=0)),
        "recall": float(recall_score(expected, predicted, zero_division=0)),
        "f1": float(f1_score(expected, predicted, zero_division=0)),
        "confusionMatrix": {
            "trueNegative": int(tn),
            "falsePositive": int(fp),
            "falseNegative": int(fn),
            "truePositive": int(tp),
        },
    }


def train(dataset_path: Path = DATASET_PATH) -> TrainingOutput:
    features, labels, rows = load_dataset(dataset_path)
    train_x, remaining_x, train_y, remaining_y = train_test_split(
        features,
        labels,
        test_size=0.30,
        random_state=42,
        stratify=labels,
    )
    validation_x, test_x, validation_y, test_y = train_test_split(
        remaining_x,
        remaining_y,
        test_size=0.50,
        random_state=42,
        stratify=remaining_y,
    )

    scaler = StandardScaler()
    train_scaled = scaler.fit_transform(train_x)
    validation_scaled = scaler.transform(validation_x)
    test_scaled = scaler.transform(test_x)

    model = Perceptron(
        max_iter=3000,
        tol=1e-5,
        random_state=42,
        eta0=0.1,
        shuffle=True,
    )
    model.fit(train_scaled, train_y)

    split_metrics = {
        "train": calculate_metrics(train_y, model.predict(train_scaled)),
        "validation": calculate_metrics(validation_y, model.predict(validation_scaled)),
        "test": calculate_metrics(test_y, model.predict(test_scaled)),
    }
    if split_metrics["test"]["accuracy"] < 0.95 or split_metrics["test"]["f1"] < 0.95:
        raise RuntimeError("Model gagal memenuhi accuracy dan F1 minimum 0.95.")

    validation_margins = np.abs(model.decision_function(validation_scaled))
    borderline_margin = float(np.percentile(validation_margins, 10))
    dataset_sha = canonical_dataset_sha256(dataset_path)

    artifact = {
        "schemaVersion": 1,
        "modelVersion": "1.0.0",
        "datasetSha256": dataset_sha,
        "featureOrder": FEATURE_ORDER,
        "means": [float(value) for value in scaler.mean_],
        "scales": [float(value) for value in scaler.scale_],
        "weights": [float(value) for value in model.coef_[0]],
        "intercept": float(model.intercept_[0]),
        "classLabels": {"0": "Risiko Rendah", "1": "Risiko Tinggi"},
        "borderlineMargin": borderline_margin,
    }

    all_scaled = scaler.transform(features)
    all_margins = model.decision_function(all_scaled)
    all_predictions = model.predict(all_scaled)
    parity_cases = []
    for row, prediction, margin in zip(rows, all_predictions, all_margins, strict=True):
        parity_cases.append(
            {
                "age": int(float(row["age"])),
                "systolicBp": float(row["systolic_bp"]),
                "totalCholesterol": int(float(row["total_cholesterol"])),
                "isCurrentSmoker": row["smoking"] == "1",
                "expectedClass": int(prediction),
                "expectedMargin": float(margin),
            }
        )

    metrics = {
        "schemaVersion": 1,
        "datasetRows": len(rows),
        "datasetSha256": dataset_sha,
        "split": {"train": len(train_x), "validation": len(validation_x), "test": len(test_x)},
        "model": "Perceptron",
        "randomState": 42,
        "borderlinePercentile": 10,
        "borderlineMargin": borderline_margin,
        "metrics": split_metrics,
        "limitations": [
            "Target risk_label dibuat dari aturan linear sintetis.",
            "Metrik mengukur kesesuaian terhadap label sintetis, bukan validitas klinis.",
            "Model hanya berlaku pada rentang data yang didokumentasikan aplikasi.",
        ],
    }
    return TrainingOutput(
        artifact=artifact,
        parity_fixture={"schemaVersion": 1, "cases": parity_cases},
        metrics=metrics,
    )


def write_json(path: Path, payload: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(payload, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def write_report(path: Path, metrics: dict) -> None:
    test = metrics["metrics"]["test"]
    confusion = test["confusionMatrix"]
    report = f"""# Evaluasi Model RiskCalc V1

## Ringkasan

- Dataset: `ml/data/dataset.csv`
- Jumlah data: {metrics['datasetRows']}
- Model: Perceptron dengan StandardScaler
- Split deterministik: {metrics['split']['train']} train / {metrics['split']['validation']} validation / {metrics['split']['test']} test
- SHA-256 dataset: `{metrics['datasetSha256']}`

## Metrik Test

| Metrik | Nilai |
|---|---:|
| Accuracy | {test['accuracy']:.4f} |
| Precision | {test['precision']:.4f} |
| Recall | {test['recall']:.4f} |
| F1-score | {test['f1']:.4f} |

Confusion matrix: TN={confusion['trueNegative']}, FP={confusion['falsePositive']}, FN={confusion['falseNegative']}, TP={confusion['truePositive']}.

Prediksi dianggap dekat batas jika nilai absolut decision margin tidak lebih dari `{metrics['borderlineMargin']:.6f}`, yaitu persentil ke-10 margin pada validation set.

## Batasan

`risk_label` dibentuk dari aturan linear sintetis dengan threshold `0.48`. Karena itu, metrik tinggi hanya menunjukkan kemampuan model meniru label sintetis dan tidak boleh diklaim sebagai akurasi klinis atau diagnosis penyakit jantung.
"""
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(report, encoding="utf-8")


def main() -> None:
    output = train()
    write_json(ARTIFACT_PATH, output.artifact)
    write_json(PARITY_PATH, output.parity_fixture)
    write_json(METRICS_PATH, output.metrics)
    write_report(REPORT_PATH, output.metrics)
    test_metrics = output.metrics["metrics"]["test"]
    print(f"Dataset rows: {output.metrics['datasetRows']}")
    print(f"Test accuracy: {test_metrics['accuracy']:.4f}")
    print(f"Test F1: {test_metrics['f1']:.4f}")
    print(f"Artifact: {ARTIFACT_PATH}")


if __name__ == "__main__":
    main()
