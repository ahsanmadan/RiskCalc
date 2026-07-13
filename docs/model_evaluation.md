# Evaluasi Model RiskCalc V1

## Ringkasan

- Dataset: `ml/data/dataset.csv`
- Jumlah data: 4159
- Model: Perceptron dengan StandardScaler
- Split deterministik: 2911 train / 624 validation / 624 test
- SHA-256 dataset: `99ff9e7c23ebbaecee90b9d7f3e6745609d01f1c8e0493132b89e85d73ea109f`

## Metrik Test

| Metrik | Nilai |
|---|---:|
| Accuracy | 0.9888 |
| Precision | 0.9957 |
| Recall | 0.9747 |
| F1-score | 0.9851 |

Confusion matrix: TN=386, FP=1, FN=6, TP=231.

Prediksi dianggap dekat batas jika nilai absolut decision margin tidak lebih dari `0.763296`, yaitu persentil ke-10 margin pada validation set.

## Batasan

`risk_label` dibentuk dari aturan linear sintetis dengan threshold `0.48`. Karena itu, metrik tinggi hanya menunjukkan kemampuan model meniru label sintetis dan tidak boleh diklaim sebagai akurasi klinis atau diagnosis penyakit jantung.
