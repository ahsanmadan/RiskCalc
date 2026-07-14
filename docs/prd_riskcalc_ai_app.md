# Product Requirements Document: RiskCalc V1

- Version: 1.1
- Platform: Android
- Positioning: demo akademik untuk pengguna umum
- Product name: RiskCalc
- Subtitle: Simulasi Skrining Risiko Jantung

## 1. Tujuan Produk

RiskCalc membantu pengguna memahami pola empat faktor risiko melalui simulasi klasifikasi sederhana. Produk harus mudah dipresentasikan, dapat berjalan tanpa server, dan tidak boleh diposisikan sebagai diagnosis atau prediksi klinis.

Target V1 dianggap tercapai jika pengguna dapat menyelesaikan alur pembuka, empat pertanyaan, dan hasil tanpa bantuan; prediksi berjalan offline; validasi serta guardrail tampil benar; dan APK release dapat dipasang.

## 2. Pengguna dan Scope

Pengguna utama adalah masyarakat umum yang mencoba aplikasi dalam konteks demo akademik. Bahasa produk hanya Bahasa Indonesia.

Fitur V1:

- Halaman pembuka dan disclaimer setiap cold start.
- Stepper: usia, merokok aktif, sistolik, kolesterol total.
- Validasi per langkah dan bantuan input melalui bottom sheet.
- Inferensi Perceptron lokal.
- Hasil risiko lebih rendah/tinggi, penjelasan faktor, tips umum, catatan borderline, dan guardrail sistolik.
- Ubah data dan mulai pemeriksaan baru.

Tidak termasuk V1: Firebase, login, histori, penyimpanan permanen, sharing, PDF, confidence numerik, backend API, Play Store, diagnosis, resep, dan terapi personal.

## 3. Alur dan Perilaku

```text
Pembuka -> Stepper 1/4 sampai 4/4 -> Hasil
```

| Input | Format | Rentang valid |
|---|---|---|
| Usia | Integer | 32-70 tahun |
| Merokok aktif | Ya/Tidak | Wajib dipilih |
| Sistolik | Desimal, koma/titik | 90-220 mmHg |
| Kolesterol total | Integer | 124-398 mg/dL |

Nilai di luar rentang training diblokir. Input tidak boleh diestimasi otomatis.

Hasil memakai frasa `Risiko lebih rendah` atau `Risiko lebih tinggi berdasarkan data yang dimasukkan`. Confidence tidak ditampilkan. Hasil dekat decision boundary tetap biner dan mendapat catatan transparansi.

Jika sistolik lebih dari 180 mmHg, aplikasi menampilkan safety alert sebelum kartu hasil: ukur ulang setelah satu menit, hubungi tenaga medis jika tetap tinggi, dan cari bantuan darurat bila disertai gejala serius. Kategori mengacu pada [American Heart Association](https://www.heart.org/en/health-topics/high-blood-pressure/understanding-blood-pressure-readings). Kategori kolesterol mengacu pada [MedlinePlus](https://medlineplus.gov/ency/article/007813.htm).

## 4. AI dan Data

Target V1 adalah `risk_label` sintetis, bukan `ten_year_chd`. Pipeline memakai split deterministik dan stratified 70/15/15, StandardScaler, lalu Perceptron.

Acceptance model:

- Accuracy test minimal 0.95.
- F1 test minimal 0.95.
- Prediksi dan decision margin Python/Kotlin identik dalam toleransi `1e-9` untuk 4.159 fixture.
- Borderline threshold adalah persentil ke-10 nilai absolut margin validation.
- Artefak memuat schema/model version, dataset SHA-256, feature order, mean, scale, weights, intercept, label, dan borderline margin.

Metrik hanya menunjukkan kesesuaian terhadap label sintetis. Tim dilarang mengklaim akurasi klinis, probabilitas penyakit, atau kelayakan diagnosis.

## 5. UX, Privasi, dan Aksesibilitas

- Material 3 dengan dynamic color Android 12+ dan fallback teal-hijau.
- Light/dark mengikuti sistem.
- Dukungan ponsel portrait/landscape dan tablet dasar dengan lebar konten maksimum.
- Touch target minimal 48dp, informasi tidak hanya melalui warna, urutan fokus logis, dan dukungan font scaling.
- Tidak ada permission internet, backup data, analytics, atau penyimpanan hasil.
- Data bertahan saat rotasi/process recreation dalam sesi aktif, tetapi tidak dipulihkan setelah cold start baru.

## 6. Quality dan Delivery

CI wajib menjalankan regenerasi artefak, pytest, parity/unit test Android, lint, dan assembleDebug. Release dibuat dengan keystore tim di luar Git serta dilengkapi checksum SHA-256.

Kasus wajib: kelas rendah/tinggi, borderline, sistolik di atas 180, semua batas min/maks, input kosong/non-numerik/out-of-range, koma desimal, edit data, reset, rotasi, serta cold start tanpa data lama.

Distribusi V1 dilakukan melalui GitHub Release sebagai APK signed. Usability test V1 dilakukan oleh tim internal sebelum demo.
