# Progress Log - RiskCalc Mobile App

Dokumen ini mencatat riwayat perkembangan project RiskCalc secara berkala (per tanggal dan per pencapaian) agar riwayat pengerjaan tetap terdokumentasi dengan baik saat di-push ke Git.

---

## 📈 Status Saat Ini (Current Status)
- **Terakhir Diperbarui**: 8 Juli 2026
- **Status Koneksi**: Samsung S21+ (`device`) sukses terhubung & terotorisasi melalui USB Debugging (Auto Blocker dinonaktifkan). Port forwarding ADB reverse (`tcp:8000`) berjalan lancar.
- **Status Aplikasi**: Aplikasi terintegrasi penuh dengan backend FastAPI & model Machine Learning Perceptron. Fitur formulir input, status loading, visualisasi dinamis kelas risiko (merah/hijau), dan networking via Retrofit sudah berfungsi penuh.
- **Library Terpasang**: Jetpack Compose, Material 3, Retrofit (HTTP), Gson Converter (Parser JSON), Lifecycle ViewModel.

---

## 📅 Log Perjalanan (Changelog & Progress)

### 8 Juli 2026 - Setup Lingkungan, Arsitektur MVVM & Integrasi FastAPI
- [x] **Konfigurasi HP & USB Debugging**: Berhasil mengatasi kendala otorisasi USB debugging pada Samsung S21+ (masalah disebabkan oleh fitur keamanan *Auto Blocker* yang secara bawaan memblokir port ADB).
- [x] **Perbaikan Build Error**: Mengatasi error kompilasi `@Composable` pada component `TopAppBar` (Material 3) dengan menambahkan anotasi `@OptIn(ExperimentalMaterial3Api::class)`.
- [x] **Instalasi Dependency Baru**:
  - Menambahkan **Retrofit** & **Gson Converter** untuk persiapan koneksi ke API backend (FastAPI).
  - Menambahkan **Lifecycle ViewModel Compose** untuk manajemen state aplikasi menggunakan arsitektur MVVM.
- [x] **Implementasi Arsitektur MVVM Android**:
  - Membuat model data [PredictRequest.kt](file:///d:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/data/remote/dto/PredictRequest.kt) & [PredictResponse.kt](file:///d:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/data/remote/dto/PredictResponse.kt).
  - Membuat interface HTTP API [ApiService.kt](file:///d:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/data/remote/ApiService.kt), client [ApiClient.kt](file:///d:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/data/remote/ApiClient.kt), dan [RiskRepository.kt](file:///d:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/data/repository/RiskRepository.kt).
  - Membuat [RiskViewModel.kt](file:///d:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/ui/viewmodel/RiskViewModel.kt) untuk menyimpan input form, status loading, dan memicu request API secara asinkron.
  - Memperbarui [RiskCalcApp.kt](file:///d:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/ui/RiskCalcApp.kt) agar UI terhubung ke ViewModel, menampilkan indikator loading, dan mengubah warna hasil prediksi berdasarkan kelas risiko (hijau untuk risiko rendah, merah untuk risiko tinggi).
- [x] **Pembangunan Backend API & ML Perceptron**:
  - Menyiapkan dependensi Python di [requirements.txt](file:///d:/Kampus/project/ai/RiskCalc/backend/requirements.txt).
  - Membuat script training [train.py](file:///d:/Kampus/project/ai/RiskCalc/backend/ml/train.py) untuk melatih model classifier linier Perceptron dengan dataset sintetis dan menstandarisasi fitur menggunakan `StandardScaler`.
  - Membuat server API menggunakan FastAPI di [main.py](file:///d:/Kampus/project/ai/RiskCalc/backend/main.py) dengan endpoint `/predict` (prediksi real-time) dan `/health` (pengecekan load model).
- [x] **Dokumentasi & Git**:
  - Mengatur ulang file [.gitignore](file:///d:/Kampus/project/ai/RiskCalc/.gitignore) untuk mengabaikan build artifacts Android dan virtual environment Python (`backend/.venv/`).
  - Memperbarui [README.md](file:///d:/Kampus/project/ai/RiskCalc/README.md) dengan panduan lengkap running backend uvicorn, sinkronisasi port ADB reverse, dan rencana roadmap baru.
  - Membuat file [PROGRESS.md](file:///d:/Kampus/project/ai/RiskCalc/PROGRESS.md) (dokumen ini) untuk mencatat aktivitas harian.

---

## 📋 Rencana Langkah Berikutnya (Next Steps)
1. **Refactoring UI Komponen**:
   - Memisahkan form input menjadi file komponen modular terpisah (`RiskInputField.kt` & `RiskResultCard.kt`).
2. **Validasi Input**:
   - Menambahkan validasi data input di sisi client Android sebelum mengirimkan data ke backend (misalnya pengecekan input kosong, format angka, dll).
3. **Pengujian Integrasi**:
   - Menambahkan unit testing untuk `RiskViewModel` dan endpoints FastAPI.
