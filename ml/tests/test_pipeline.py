from pathlib import Path

import numpy as np

from ml.train_export import FEATURE_ORDER, load_dataset, train


def test_dataset_schema_ranges_and_labels() -> None:
    features, labels, rows = load_dataset()

    assert len(rows) == 4159
    assert features.shape == (4159, len(FEATURE_ORDER))
    assert set(np.unique(labels)) == {0, 1}
    assert features[:, 0].min() == 32
    assert features[:, 0].max() == 70
    assert features[:, 1].min() == 90
    assert features[:, 1].max() == 220
    assert features[:, 2].min() == 124
    assert features[:, 2].max() == 398


def test_training_meets_acceptance_and_exports_complete_schema() -> None:
    output = train()
    artifact = output.artifact
    test_metrics = output.metrics["metrics"]["test"]

    assert test_metrics["accuracy"] >= 0.95
    assert test_metrics["f1"] >= 0.95
    assert artifact["schemaVersion"] == 1
    assert artifact["featureOrder"] == FEATURE_ORDER
    assert len(artifact["means"]) == 4
    assert len(artifact["scales"]) == 4
    assert len(artifact["weights"]) == 4
    assert artifact["borderlineMargin"] > 0
    assert len(output.parity_fixture["cases"]) == 4159


def test_repository_dataset_is_outside_android_assets() -> None:
    root = Path(__file__).resolve().parents[2]
    assert (root / "ml" / "data" / "dataset.csv").exists()
    assert not (root / "app" / "src" / "main" / "assets" / "dataset.csv").exists()
