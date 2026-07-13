# Audit Acceptance RiskCalc V1

Audit ulang dilakukan terhadap rencana implementasi V1.

## Otomatis

- Pipeline Python: validasi schema/rentang/label, split stratified deterministik, metrik, artefak, dan checksum lintas platform.
- Parity: kelas dan decision margin Python-Kotlin untuk seluruh 4.159 baris.
- ViewModel: batas minimum/maksimum, input kosong/huruf/desimal, koma/titik, nilai di luar rentang, back, edit, reset, sesi baru, dan restore state.
- UI emulator API 36: cold start, disclaimer, serta guardrail sistolik di atas 180 sebelum halaman hasil.
- Delivery: unit test, lint, `assembleDebug`, release signing, checksum APK, dan GitHub Actions.

## Pemeriksaan Emulator

- Portrait dan landscape dapat di-scroll.
- Font scale 200% mempertahankan konten dan aksi melalui scroll.
- Simulasi lebar tablet memakai konten maksimum 720dp.
- Light/dark mengikuti mode sistem; dynamic color memiliki fallback tema teal-hijau.
- Hierarki aksesibilitas memuat label teks, status non-warna, fokus tombol, dan kontrol Material minimum 48dp.

## Batas Klaim

Metrik hanya mengukur kesesuaian dengan label sintetis threshold `0.48`. Audit ini bukan validasi klinis dan bukan pengganti uji pengguna manusia dengan TalkBack pada perangkat fisik.
