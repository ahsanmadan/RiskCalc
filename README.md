# RiskCalc Mobile App

Starter project aplikasi mobile Android native untuk RiskCalc menggunakan Kotlin dan Jetpack Compose.

## Stack Teknologi & Library

- **Kotlin**: Bahasa pemrograman modern utama untuk Android.
- **Jetpack Compose**: Toolkit deklaratif modern untuk merancang antarmuka (UI).
- **Material 3**: Panduan desain UI terbaru dari Google.
- **Retrofit (2.11.0)**: Library HTTP client untuk melakukan komunikasi data dengan API backend (FastAPI).
- **Gson Converter (2.11.0)**: Digunakan bersama Retrofit untuk mem-parse data JSON dari API menjadi objek Kotlin secara otomatis.
- **Lifecycle ViewModel Compose (2.10.0)**: Library untuk mengintegrasikan arsitektur MVVM (Model-View-ViewModel) agar manajemen state data di UI menjadi aman dan sesuai standar Android Jetpack.

---

## Struktur Project

- `app/` -> Modul Android utama.
  - `src/main/java/com/riskcalc/mobile/` -> Kode sumber utama Kotlin.
    - `MainActivity.kt` -> Entry point utama aplikasi.
    - `ui/RiskCalcApp.kt` -> Desain layout form input dan logika UI.
    - `ui/theme/` -> Konfigurasi tema warna, tipografi, dan gaya visual.

---

## Panduan Menjalankan Aplikasi (Running/Testing)

Kamu bisa menjalankan aplikasi ini langsung di **HP Android fisik** (sebagai pengganti emulator agar lebih ringan) menggunakan dua metode: via Android Studio atau langsung lewat Terminal.

### Persiapan HP Android (Contoh: Samsung S21+ / One UI)

Sebelum menjalankan aplikasi, kamu wajib mengaktifkan fitur debugging di HP kamu:

1. **Aktifkan Opsi Pengembang (Developer Options):**
   - Buka **Pengaturan (Settings)** -> **Tentang ponsel (About phone)** -> **Informasi perangkat lunak (Software information)**.
   - Ketuk **Nomor versi (Build number)** sebanyak **7 kali** sampai muncul tulisan "Mode pengembang telah diaktifkan".
   - Jika diminta, masukkan PIN/Kata Kunci layar kunci HP-mu.

2. **Aktifkan USB Debugging:**
   - Kembali ke menu utama **Pengaturan**.
   - Pilih menu paling bawah: **Pilihan pengembang (Developer options)**.
   - Cari dan aktifkan opsi **Proses debug USB (USB debugging)**.

3. **Khusus Samsung (Penting!): Matikan Auto Blocker:**
   - Jika HP Samsung-mu menggunakan One UI 6 ke atas, buka **Pengaturan** -> **Keamanan dan privasi (Security and privacy)**.
   - Pilih **Pemblokir Otomatis (Auto Blocker)** dan pastikan statusnya **Nonaktif (OFF)**. Jika aktif, laptop tidak akan bisa mengirim aplikasi ke HP.

4. **Hubungkan HP ke Laptop:**
   - Sambungkan menggunakan kabel data USB.
   - Setel mode USB ke **Mentransfer file (Transferring files)**.
   - Buka kunci HP, tunggu pop-up **"Izinkan debugging USB?"** muncul di layar HP, centang *"Selalu izinkan dari komputer ini"*, lalu ketuk **Izinkan (Allow)**.

---

### Metode A: Menjalankan Lewat Terminal (Tanpa Buka Android Studio)

Metode ini sangat disarankan setelah project pernah dibuka minimal sekali di Android Studio (untuk membuat file `gradlew.bat`).

Buka terminal di editor kamu (seperti Antigravity/VS Code) dan jalankan perintah berikut:

**Di Windows (PowerShell):**
```powershell
# 1. Daftarkan path compiler Java bawaan Android Studio
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"

# 2. Build dan install aplikasi langsung ke HP kamu
.\gradlew.bat installDebug
```

**Di Linux / macOS (Bash):**
```bash
# 1. Daftarkan path compiler Java
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" # Sesuaikan path macOS/Linux

# 2. Jalankan perintah install
./gradlew installDebug
```

---

### Metode B: Menjalankan Lewat Android Studio UI

1. Buka folder `RiskCalc` ini menggunakan **Android Studio**.
2. Tunggu proses *Gradle Sync* selesai (status loading di pojok kanan bawah selesai).
3. Lihat ke toolbar atas Android Studio, pastikan nama HP Samsung/Android-mu sudah terdeteksi di dropdown device.
4. Klik tombol **Run** (ikon segitiga hijau ▶️) atau tekan shortcut `Shift + F10`.

---

## Roadmap Pengembangan Berikutnya

1. **Komponen UI**: Memisahkan form input ke komponen modular (`RiskInputField`, `RiskResultCard`).
2. **ViewModel**: Menerapkan arsitektur MVVM dengan `RiskViewModel` untuk mengelola state form dan data.
3. **API Integration**: Menambahkan library **Retrofit** untuk mengirim data input pasien ke backend FastAPI dan model prediksi Perceptron.
