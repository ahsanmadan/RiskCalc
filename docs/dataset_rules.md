# Aturan Dataset RiskCalc V1

## Kolom

| Kolom | Fungsi |
|---|---|
| `age` | Usia dalam tahun |
| `systolic_bp` | Tekanan darah sistolik dalam mmHg |
| `total_cholesterol` | Kolesterol total dalam mg/dL |
| `smoking` | `0` tidak merokok, `1` merokok aktif |
| `risk_label` | Target sintetis rendah/tinggi |
| `linear_risk_score` | Skor linear pembentuk label |
| `ten_year_chd` | Referensi outcome asli, bukan target V1 |

## Target dan Rentang

`risk_label` bernilai `Risiko Tinggi` jika `linear_risk_score >= 0.48`; selain itu bernilai `Risiko Rendah`. Pipeline menolak training jika ada label yang tidak sesuai aturan ini.

Rentang dataset yang menjadi batas validasi aplikasi:

- Usia: 32-70 tahun
- Sistolik: 90-220 mmHg
- Kolesterol total: 124-398 mg/dL
- Merokok: 0 atau 1

## Batasan

Label dibuat agar memiliki decision boundary linear untuk kebutuhan pembelajaran Perceptron. Dataset dan model tidak divalidasi sebagai alat klinis. Kolom `ten_year_chd` tidak dipakai sebagai target V1 karena tidak memenuhi tujuan tugas linear separability dan memiliki makna berbeda dari `risk_label`.
