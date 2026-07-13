# Evaluasi Jujur RiskCalc V1

## Kekuatan

- Inferensi berjalan offline dan tidak mengirim data kesehatan.
- Dataset, training, artefak, metrik, dan parity test dapat direproduksi.
- Android dan Python memakai formula inferensi yang sama untuk seluruh fixture dataset.
- Copy produk membedakan simulasi edukatif dari diagnosis medis.

## Keterbatasan Kritis

- Label target sintetis dan diturunkan dari aturan linear. Akurasi tinggi tidak membuktikan performa klinis.
- Empat fitur tidak cukup untuk penilaian medis komprehensif.
- Kategori tekanan darah hanya memakai sistolik; diagnosis tekanan darah membutuhkan konteks dan pengukuran lengkap.
- Tips bersifat umum dan bukan rekomendasi terapi individual.

## Pengembangan Setelah V1

- Validasi dataset serta target dengan pembimbing dan tenaga kesehatan.
- Evaluasi outcome nyata dengan metodologi yang sesuai dan pelaporan bias/kalibrasi.
- Pertimbangkan Firebase hanya setelah kebutuhan akun, consent, retensi data, dan aturan akses disepakati.
- Lakukan usability test dengan peserta di luar tim sebelum distribusi publik.
