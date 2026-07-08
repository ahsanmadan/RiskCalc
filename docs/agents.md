# RiskCalc — Agents & Modules Plan

Dokumen ini mendeskripsikan seluruh komponen (agent/modul) sistem RiskCalc,
tanggung jawabnya, target deliverable, dan ketergantungan antar komponen.

---

## Gambaran Arsitektur Sistem

```
┌────────────────────────────────────────────────────────────┐
│                  Android App (Kotlin)                       │
│    Jetpack Compose · Material 3 · Retrofit · ViewModel      │
│                                                            │
│  [Form Input]  →  [ViewModel]  →  [API Client (Retrofit)]  │
│                                  ↓                         │
│                         [Result Screen]                     │
└───────────────────────────┬────────────────────────────────┘
                            │  HTTP POST /predict (JSON)
                            ↓
┌────────────────────────────────────────────────────────────┐
│                   FastAPI Backend (Python)                  │
│              Uvicorn · Pydantic · scikit-learn             │
│                                                            │
│  [POST /predict]  →  [Validator]  →  [ML Engine]           │
│                                         ↓                  │
│                              [PredictionResponse JSON]      │
└───────────────────────────┬────────────────────────────────┘
                            │  predict(features)
                            ↓
┌────────────────────────────────────────────────────────────┐
│                 ML Engine (Python — Perceptron)             │
│           scikit-learn · joblib · StandardScaler           │
│                                                            │
│  [model.pkl]  +  [scaler.pkl]  →  "Risiko Tinggi/Rendah"  │
└────────────────────────────────────────────────────────────┘
```

---

## Agent 1 — ML Training Pipeline

| Item        | Detail |
|-------------|--------|
| **Nama**    | `ml_pipeline` |
| **Bahasa**  | Python 3.11+ |
| **Lokasi**  | `backend/ml/` |
| **Tujuan**  | Melatih model Perceptron dan menyimpannya sebagai artefak siap pakai |

### Tanggung Jawab
- Memuat dan membersihkan dataset (Framingham Heart Study / UCI Heart)
- Melakukan preprocessing: normalisasi fitur dengan `StandardScaler`
- Melatih model `Perceptron` (linearly separable) dari `scikit-learn`
- Mengevaluasi performa: akurasi, precision, recall, F1, confusion matrix
- Menyimpan artefak `model.pkl` dan `scaler.pkl` via `joblib`

### Input Fitur
| Fitur                    | Tipe     | Keterangan                  |
|--------------------------|----------|-----------------------------|
| `age`                    | `int`    | Usia pasien (tahun)         |
| `systolic_bp`            | `int`    | Tekanan darah sistolik (mmHg) |
| `total_cholesterol`      | `float`  | Kadar kolesterol total (mg/dL) |
| `smoking_status`         | `int`    | Status merokok: 0 = Tidak, 1 = Ya |

### Output
| Label           | Kelas | Keterangan           |
|-----------------|-------|----------------------|
| `Risiko Tinggi` | `1`   | Berisiko tinggi PJK  |
| `Risiko Rendah` | `0`   | Berisiko rendah PJK  |

### Target File
```
backend/
└── ml/
    ├── train.py           # Script training utama
    ├── evaluate.py        # Evaluasi dan laporan metrik
    ├── dataset/           # Dataset CSV (Framingham/UCI)
    └── artifacts/
        ├── model.pkl      # Model Perceptron terlatih
        └── scaler.pkl     # StandardScaler yang sudah fit
```

---

## Agent 2 — FastAPI Backend

| Item        | Detail |
|-------------|--------|
| **Nama**    | `riskcalc-api` |
| **Bahasa**  | Python 3.11+ |
| **Framework** | FastAPI + Uvicorn |
| **Lokasi**  | `backend/` |
| **Port**    | `8000` (dev) |
| **Tujuan**  | Menyediakan REST API endpoint prediksi yang dikonsumsi oleh Android app |

### Tanggung Jawab
- Memuat `model.pkl` dan `scaler.pkl` saat startup
- Menyediakan endpoint `POST /predict` dengan validasi input via Pydantic
- Mengembalikan hasil prediksi dalam format JSON
- Menyediakan endpoint `GET /health` untuk cek status server
- Handle CORS agar bisa diakses dari device Android (emulator maupun fisik)

### Endpoint

#### `GET /health`
```json
// Response 200
{ "status": "ok", "model_loaded": true }
```

#### `POST /predict`
```json
// Request Body
{
  "age": 45,
  "systolic_bp": 140,
  "total_cholesterol": 230.5,
  "smoking_status": 1
}

// Response 200
{
  "prediction": 1,
  "label": "Risiko Tinggi",
  "confidence": 0.87
}

// Response 422 — Validasi gagal
{
  "detail": [{ "loc": ["body", "age"], "msg": "value is not a valid integer" }]
}
```

### Target File
```
backend/
├── main.py                # Entry point FastAPI app
├── routers/
│   └── predict.py         # Router untuk endpoint /predict
├── schemas/
│   ├── request.py         # PredictRequest (Pydantic model)
│   └── response.py        # PredictResponse (Pydantic model)
├── services/
│   └── predictor.py       # Logic load model + prediksi
├── ml/
│   ├── train.py
│   ├── evaluate.py
│   ├── dataset/
│   └── artifacts/
│       ├── model.pkl
│       └── scaler.pkl
├── requirements.txt
└── .env                   # Konfigurasi environment
```

### Dependencies (`requirements.txt`)
```
fastapi>=0.111.0
uvicorn[standard]>=0.29.0
pydantic>=2.7.0
scikit-learn>=1.4.0
joblib>=1.4.0
numpy>=1.26.0
python-dotenv>=1.0.0
```

---

## Agent 3 — Android Mobile App (Kotlin)

| Item        | Detail |
|-------------|--------|
| **Nama**    | `riskcalc-mobile` |
| **Bahasa**  | Kotlin |
| **Framework** | Jetpack Compose + Material 3 |
| **Lokasi**  | `app/` (sudah ada) |
| **Tujuan**  | UI mobile untuk input data pasien dan menampilkan hasil prediksi risiko PJK |

### Tanggung Jawab
- Form input 4 fitur: usia, sistolik, kolesterol, status merokok
- Validasi input sisi klien (range check, required field)
- Mengirim request ke FastAPI via HTTP (Retrofit2 + OkHttp)
- Menampilkan hasil: label risiko, warna indikator (merah/hijau), nilai confidence
- Handle loading state dan error (koneksi gagal, server error)

### Arsitektur (MVVM)
```
ui/
├── RiskCalcApp.kt          # Root Composable (sudah ada)
├── screens/
│   ├── InputScreen.kt      # Form input 4 fitur
│   └── ResultScreen.kt     # Tampilan hasil prediksi
├── components/
│   ├── RiskInputField.kt   # Reusable input component
│   └── RiskResultCard.kt   # Card hasil prediksi
├── viewmodel/
│   └── RiskViewModel.kt    # ViewModel: state + API call
└── theme/
    ├── Color.kt            # (sudah ada)
    ├── Theme.kt            # (sudah ada)
    └── Type.kt             # (sudah ada)

data/
├── remote/
│   ├── ApiService.kt       # Retrofit interface
│   ├── ApiClient.kt        # Retrofit + OkHttp builder
│   └── dto/
│       ├── PredictRequest.kt   # Data class request
│       └── PredictResponse.kt  # Data class response
└── repository/
    └── RiskRepository.kt   # Abstraksi antara ViewModel dan API
```

### Dependencies (Gradle)
```kotlin
// Retrofit + GSON
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

// ViewModel + LiveData
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")

// Navigation (opsional jika multi-screen)
implementation("androidx.navigation:navigation-compose:2.7.7")
```

---

## Agent 4 — Dokumentasi & Pengujian

| Item        | Detail |
|-------------|--------|
| **Nama**    | `docs-test` |
| **Lokasi**  | `docs/` dan `backend/tests/` |
| **Tujuan**  | Memastikan sistem teruji dan terdokumentasi untuk keperluan akademis |

### Tanggung Jawab
- Unit test endpoint FastAPI dengan `pytest` + `httpx`
- Test prediksi model (assert output benar untuk input yang diketahui)
- Dokumentasi API otomatis via Swagger UI (bawaan FastAPI di `/docs`)
- Laporan evaluasi model (akurasi, confusion matrix) → `docs/model_evaluation.md`

### Target File
```
backend/tests/
├── test_api.py        # Test endpoint /predict dan /health
└── test_model.py      # Test output Perceptron untuk kasus uji

docs/
├── agents.md          # ← File ini
├── api_spec.md        # Spesifikasi API lengkap
├── model_evaluation.md # Hasil evaluasi model (akurasi, dll)
└── jurnal/            # Kumpulan jurnal referensi (sudah ada)
```

---

## Urutan Pengerjaan (Dependency Order)

```
[1] ML Training Pipeline   →  Hasilkan model.pkl & scaler.pkl
         ↓
[2] FastAPI Backend        →  Load artefak, buat endpoint /predict
         ↓
[3] Android Mobile App     →  Konsumsi API, tampilkan hasil
         ↓
[4] Dokumentasi & Testing  →  Validasi end-to-end
```

---

## Konfigurasi URL Backend di Android

| Environment | URL Base API |
|-------------|--------------|
| **Emulator Android** | `http://10.0.2.2:8000` |
| **Device Fisik (USB)** | `http://<IP_PC_lokal>:8000` |
| **Produksi (opsional)** | `https://api.riskcalc.example.com` |

> [!NOTE]
> Ganti `<IP_PC_lokal>` dengan IP komputer di jaringan WiFi yang sama.
> Contoh: `http://192.168.1.5:8000`

---

## Ringkasan Target Deliverable

| No | Agent | Output Utama | Status |
|----|-------|-------------|--------|
| 1 | ML Pipeline | `model.pkl`, `scaler.pkl`, laporan evaluasi | ✅ Selesai |
| 2 | FastAPI Backend | Server jalan di port 8000, endpoint `/predict` aktif | ✅ Selesai |
| 3 | Android App | APK terinstall di device, bisa input + tampilkan hasil | ✅ Selesai |
| 4 | Docs & Tests | Swagger UI, test passing, `model_evaluation.md` | ⬜ Sebagian |
