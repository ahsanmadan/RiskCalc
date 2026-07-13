# Product Requirements Document

## RiskCalc AI App

- Version: 1.0
- Date: 2026-07-13
- Product: RiskCalc
- Platform: Android mobile app
- Document owner: Product / Engineering team RiskCalc

## 1. Executive Summary

RiskCalc adalah aplikasi Android untuk membantu pengguna melakukan skrining awal risiko penyakit jantung koroner menggunakan empat input dasar: usia, tekanan darah sistolik, kolesterol total, dan status merokok.

Produk ini memanfaatkan model machine learning sederhana yang sesuai dengan kebutuhan tugas akademik, mudah dijelaskan, dan dapat dijalankan secara ringan pada alur aplikasi. Output utama aplikasi adalah klasifikasi `Risiko Rendah` atau `Risiko Tinggi`, disertai penjelasan singkat dan disclaimer bahwa hasil ini bukan diagnosis medis.

Tujuan MVP adalah menghadirkan aplikasi yang:

- mudah dipakai di perangkat Android,
- dapat memproses input pengguna dengan cepat,
- memberikan hasil klasifikasi yang konsisten,
- memakai dataset lokal yang sudah disiapkan,
- dan cukup aman untuk demonstrasi akademik.

## 2. Problem Statement

Banyak orang tidak memahami faktor risiko dasar penyakit jantung koroner dan tidak punya alat sederhana untuk melakukan skrining awal secara cepat. Pemeriksaan medis tetap menjadi standar utama, tetapi untuk kebutuhan edukasi dan tugas akademik dibutuhkan aplikasi yang:

- menerima input kesehatan dasar,
- menghitung risiko secara otomatis,
- menampilkan hasil yang mudah dipahami,
- dan menjelaskan keterbatasan sistem.

Masalah yang ingin diselesaikan bukan diagnosis klinis, tetapi edukasi dan skrining awal berbasis data.

## 3. Goals

### Product Goals

- Menyediakan pengalaman input data kesehatan yang sederhana di Android.
- Menampilkan hasil risiko dalam waktu singkat setelah pengguna menekan tombol prediksi.
- Menyediakan output yang mudah dipahami untuk pengguna umum.
- Mendukung presentasi/demo akademik dengan alur yang stabil dan jelas.

### AI / ML Goals

- Menggunakan model klasifikasi yang ringan, dapat dijelaskan, dan cocok untuk dataset yang disusun.
- Menghasilkan prediksi biner yang konsisten untuk input yang sama.
- Menjaga kualitas prediksi minimum pada threshold evaluasi yang telah ditetapkan.

### Academic Goals

- Menunjukkan penggunaan dataset, model, aplikasi mobile, dan dokumentasi sebagai satu sistem utuh.
- Menjaga ruang lingkup tetap realistis untuk tugas semester.

## 4. Non-Goals

- Bukan alat diagnosis medis resmi.
- Bukan pengganti dokter, laboratorium, atau pemeriksaan klinis.
- Tidak menampilkan rekomendasi terapi, resep, atau saran medis personal yang detail.
- Tidak memproses rekam medis lengkap, EKG, atau biomarker lanjutan.
- Tidak mendukung akun pengguna, sinkronisasi cloud, atau histori multi-user pada MVP.

## 5. Target Users

### Primary Users

- Mahasiswa dan dosen untuk demo/presentasi akademik.
- Pengguna umum yang ingin melihat contoh skrining risiko sederhana.

### Secondary Users

- Reviewer proyek.
- Penguji tugas akhir / mata kuliah.

## 6. User Jobs To Be Done

- Sebagai pengguna, saya ingin memasukkan data kesehatan dasar dengan cepat.
- Sebagai pengguna, saya ingin mendapatkan hasil risiko yang mudah dipahami.
- Sebagai penguji, saya ingin melihat bahwa aplikasi dapat berjalan stabil dari input sampai output.
- Sebagai tim proyek, kami ingin memiliki aplikasi yang mudah dijelaskan dari sisi dataset, model, dan UI.

## 7. Product Scope

### In Scope for MVP

- Android app dengan form input:
  - usia
  - tekanan darah sistolik
  - kolesterol total
  - status merokok
- Validasi input dasar di sisi aplikasi.
- Tombol prediksi.
- Tampilan hasil klasifikasi:
  - Risiko Rendah
  - Risiko Tinggi
- Penjelasan singkat hasil.
- Disclaimer medis.
- Dataset lokal di repo.
- Dokumentasi produk dan arsitektur dasar.

### Next Scope

- Menampilkan confidence score atau tingkat keyakinan model.
- Penjelasan faktor yang paling memengaruhi hasil.
- Integrasi backend/API jika inferensi dipindahkan dari lokal.
- Penyimpanan histori prediksi.

## 8. User Flow

1. Pengguna membuka aplikasi RiskCalc.
2. Pengguna melihat form input utama.
3. Pengguna mengisi usia, sistolik, kolesterol, dan status merokok.
4. Sistem memvalidasi bahwa input lengkap dan masuk akal.
5. Pengguna menekan tombol `Prediksi Risiko`.
6. Sistem menjalankan model klasifikasi.
7. Sistem menampilkan:
   - label risiko,
   - penjelasan singkat,
   - disclaimer bahwa hasil bukan diagnosis.
8. Jika input tidak valid, sistem menampilkan pesan error yang jelas.

## 9. Functional Requirements

### FR-1 Input Form

Sistem harus menyediakan field input untuk:

- usia
- tekanan darah sistolik
- kolesterol total
- status merokok

### FR-2 Input Validation

Sistem harus menolak input yang:

- kosong,
- bukan angka untuk field numerik,
- berada di luar rentang yang ditentukan.

### FR-3 Prediction Trigger

Sistem harus menjalankan prediksi hanya setelah pengguna menekan tombol aksi utama.

### FR-4 Result Display

Sistem harus menampilkan hasil klasifikasi dalam bentuk teks yang jelas dan mudah dibaca.

### FR-5 Error Handling

Sistem harus menampilkan pesan saat:

- input tidak valid,
- model gagal dijalankan,
- data tidak lengkap.

### FR-6 Disclaimer

Sistem harus selalu menampilkan bahwa hasil hanya untuk skrining awal dan bukan diagnosis medis.

## 10. Non-Functional Requirements

- Waktu respons target untuk prediksi di device: kurang dari 2 detik pada skenario normal.
- UI harus tetap terbaca pada layar Android umum.
- Build debug harus dapat dihasilkan dari repo.
- Aplikasi harus tetap dapat digunakan tanpa koneksi internet jika model/inferensi lokal dipakai.
- Struktur kode harus cukup rapi untuk dipresentasikan dan dikembangkan lebih lanjut.

## 11. AI / ML Requirements

### Model Type

Untuk MVP, sistem menggunakan model klasifikasi ringan yang mudah dijelaskan. Model harus cocok untuk pembelajaran akademik dan mudah direproduksi.

### Input Features

Model memakai fitur:

- `age`
- `systolic_bp`
- `total_cholesterol`
- `smoking`

### Output

Model mengembalikan:

- label kelas: `Risiko Rendah` atau `Risiko Tinggi`
- opsional: skor atau confidence internal

### Why This AI Approach

Pendekatan AI/ML dipilih karena:

- lebih sesuai untuk tugas klasifikasi risiko dibanding rule manual yang terlalu kaku,
- dapat memperlihatkan alur data-ke-model-ke-aplikasi,
- dan tetap cukup sederhana untuk dipahami dalam konteks tugas kuliah.

## 12. Dataset Requirements

- Dataset utama harus tersimpan di repo agar reproducible.
- Dataset harus memiliki kolom fitur yang konsisten dengan input aplikasi.
- Label target harus jelas dan terdokumentasi.
- Data dummy / sintetis boleh dipakai selama masih logis dan didukung referensi faktor risiko.
- Tim harus mendokumentasikan asumsi pembentukan label dan preprocessing.

## 13. Behavior Boundaries

Sistem harus:

- memberi hasil klasifikasi sesuai format yang konsisten,
- menolak input jelas tidak valid,
- memberi penjelasan yang sederhana dan non-misleading,
- tidak berpura-pura memberikan kepastian medis.

Sistem tidak boleh:

- menyatakan bahwa pengguna pasti sakit atau pasti sehat,
- memberi saran pengobatan spesifik,
- mengklaim setara dengan alat klinis.

## 14. Guardrails and Safety

Karena domain menyentuh kesehatan, guardrail wajib:

- hasil harus diberi label sebagai skrining awal,
- hasil harus disertai disclaimer medis,
- aplikasi harus mendorong konsultasi ke tenaga medis bila pengguna khawatir,
- sistem tidak boleh menyimpan atau membagikan data sensitif tanpa kebutuhan jelas,
- copy UI harus menghindari bahasa yang menakut-nakuti atau terlalu meyakinkan.

Contoh teks minimum:

`Hasil ini hanya untuk skrining awal dan bukan diagnosis medis. Jika Anda memiliki kekhawatiran kesehatan, konsultasikan dengan tenaga medis profesional.`

## 15. Failure Modes and Fallbacks

### Failure Modes

- Input pengguna salah format.
- Model gagal dimuat.
- Prediksi gagal diproses.
- Output terlalu meyakinkan untuk kasus yang seharusnya ambigu.
- Distribusi input baru tidak sesuai dengan data pelatihan.

### Fallbacks

- Tampilkan error input yang spesifik.
- Gunakan pesan umum bila proses gagal: `Prediksi belum dapat diproses. Silakan cek kembali input Anda.`
- Jika confidence belum siap ditampilkan, jangan tampilkan angka confidence yang belum tervalidasi.
- Jika model tidak tersedia, tombol prediksi harus gagal secara aman, bukan crash.

## 16. Evaluation Plan

### Offline Evaluation

Sebelum rilis demo, model harus diuji minimal pada:

- accuracy
- precision
- recall
- F1-score
- confusion matrix

### Product Evaluation

Tim harus menyiapkan kasus uji untuk:

- input normal risiko rendah,
- input normal risiko tinggi,
- input kosong,
- input non-numerik,
- input out-of-range,
- input borderline.

### Acceptance Threshold

Target awal MVP:

- build aplikasi sukses,
- alur input sampai output berjalan tanpa crash,
- metrik model cukup untuk demonstrasi akademik,
- hasil prediksi konsisten untuk input yang sama,
- pesan error dan disclaimer tampil benar.

Jika tim ingin threshold angka, rekomendasi awal:

- accuracy internal minimal 0.75 untuk dataset kerja,
- seluruh skenario validasi input kritis harus lolos.

## 17. Success Metrics

### Business / Project Metrics

- PRD, dataset, dan app tersedia di GitHub.
- Demo aplikasi dapat dijalankan saat presentasi.

### User Metrics

- Pengguna dapat menyelesaikan satu prediksi tanpa bantuan tambahan.
- Pengguna memahami arti hasil dan disclaimer.

### Quality Metrics

- Prediksi konsisten untuk input identik.
- Tidak ada crash pada flow utama.
- Validasi input bekerja untuk skenario umum.

### Operational Metrics

- Waktu build tetap wajar.
- APK debug berhasil dihasilkan.

## 18. Rollout Plan

### Phase 1 - Foundation

- Finalisasi dataset
- Finalisasi form input
- Finalisasi klasifikasi dasar

### Phase 2 - MVP Demo

- Hubungkan input ke model
- Tampilkan hasil
- Tambahkan disclaimer dan error handling

### Phase 3 - Improvement

- Tambahkan confidence / explanation
- Tambahkan evaluasi yang lebih formal
- Rapikan dokumentasi dan presentasi

## 19. Open Questions

- Apakah inferensi final akan dijalankan lokal di app atau lewat backend?
- Apakah confidence score akan ditampilkan ke pengguna atau hanya untuk internal?
- Apakah label `ten_year_chd` akan dipakai langsung sebagai target utama atau hanya referensi tambahan?
- Apakah perlu halaman hasil terpisah dengan penjelasan lebih detail?

## 20. Acceptance Criteria Summary

PRD ini dianggap terpenuhi untuk MVP bila:

- terdapat Android app yang menerima 4 input utama,
- terdapat dataset yang tersimpan di repo,
- proses prediksi dapat berjalan end-to-end,
- hasil tampil dalam format risiko rendah/tinggi,
- disclaimer medis muncul,
- dan dokumentasi cukup jelas untuk pengembangan serta presentasi.
