# RiskCalc

RiskCalc adalah aplikasi Android offline untuk simulasi skrining faktor risiko jantung dalam konteks pembelajaran akademik. Aplikasi memakai empat input: usia, status merokok aktif, tekanan darah sistolik, dan kolesterol total.

> Hasil RiskCalc bukan diagnosis medis dan tidak menggantikan pemeriksaan tenaga kesehatan.

## Fitur V1

- Alur pembuka, stepper empat pertanyaan, dan halaman hasil.
- Validasi input sesuai rentang dataset pelatihan.
- Inferensi Perceptron langsung di perangkat tanpa server atau internet.
- Ringkasan kategori faktor, tips umum, disclaimer, dan peringatan sistolik di atas 180 mmHg.
- Dynamic color Android 12+, fallback tema teal, light/dark mode, serta layout ponsel dan tablet dasar.
- Tidak ada login, histori, cloud sync, confidence numerik, atau penyimpanan data kesehatan.

## Arsitektur

```text
Jetpack Compose UI
        |
RiskViewModel + StateFlow + SavedStateHandle
        |
RiskRepository
        |
LocalRiskEngine
        |
risk_model.json (weights + scaler + metadata)
```

Python hanya dipakai untuk training, evaluasi, dan export artefak. Dataset tidak dibundel ke APK.

## Kebutuhan

- JDK 17
- Android SDK 36
- Python 3.11+ untuk regenerasi model
- Windows PowerShell atau shell setara

## Training dan Test Model

```powershell
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r .\ml\requirements.txt
.\.venv\Scripts\python.exe .\ml\train_export.py
.\.venv\Scripts\python.exe -m pytest -q
```

Pipeline menghasilkan:

- `app/src/main/assets/risk_model.json`
- `app/src/test/resources/parity_cases.json`
- `ml/artifacts/metrics.json`
- `docs/model_evaluation.md`

## Build Android

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

APK debug tersedia di `app/build/outputs/apk/debug/app-debug.apk`.

## Release Signing

Salin `keystore.properties.example` menjadi `keystore.properties`, lalu isi path keystore dan kredensial lokal. File keystore serta `keystore.properties` diabaikan Git.

```powershell
.\gradlew.bat assembleRelease
Get-FileHash .\app\build\outputs\apk\release\app-release.apk -Algorithm SHA256
```

APK release tersedia di `app/build/outputs/apk/release/app-release.apk`.

## Batasan Akademik

Label target dibuat dari aturan linear sintetis dengan threshold `0.48`. Metrik tinggi hanya menunjukkan kemampuan Perceptron mengikuti label tersebut; metrik tidak boleh diklaim sebagai akurasi klinis, probabilitas penyakit, atau bukti kelayakan diagnosis.

Dokumentasi lengkap tersedia di `docs/prd_riskcalc_ai_app.md`, `docs/architecture.md`, `docs/dataset_rules.md`, dan `docs/model_evaluation.md`.
