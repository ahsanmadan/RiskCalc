# Modul RiskCalc V1

## 1. ML Pipeline

Lokasi: `ml/`

Tanggung jawab: membaca dataset tunggal, memvalidasi label, melakukan split stratified, melatih StandardScaler dan Perceptron, mengukur metrik, serta mengekspor artefak Android dan fixture parity.

## 2. Local Risk Engine

Lokasi: `app/src/main/java/com/riskcalc/mobile/data/local/`

Tanggung jawab: memuat artefak JSON, memvalidasi schema/urutan fitur, melakukan standardisasi, menghitung decision margin, kelas, dan status borderline tanpa jaringan.

## 3. Android Product Flow

Lokasi: `app/src/main/java/com/riskcalc/mobile/ui/`

Tanggung jawab: halaman pembuka, stepper empat input, validasi, halaman hasil, faktor edukatif, tips, guardrail sistolik, disclaimer, dan aksesibilitas dasar.

## 4. Quality and Delivery

Lokasi: test Android, `ml/tests/`, `.github/workflows/ci.yml`, dan `docs/`.

Tanggung jawab: parity Python-Kotlin 4.159 kasus, unit test, lint, build APK, dokumentasi, signing lokal, dan checksum release.

## Urutan Dependency

```text
dataset -> training pipeline -> risk_model.json -> LocalRiskEngine -> ViewModel -> Compose UI
```

FastAPI dan Retrofit tidak termasuk arsitektur aktif V1. Firebase, login, dan histori ditempatkan sebagai backlog setelah versi akademik stabil.
