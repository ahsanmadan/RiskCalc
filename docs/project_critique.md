# Kritik & Evaluasi Project RiskCalc
### Audit Jujur — Apa yang Kurang, Apa yang Bermasalah, dan Cara Perbaikannya

---

> Dokumen ini ditulis sebagai bahan evaluasi internal proyek.
> Tujuannya bukan menjatuhkan, tapi memastikan proyek ini bisa
> dipertanggungjawabkan secara akademis dan teknis.

---

## 🔴 KRITIS — Harus Diperbaiki Sekarang

### 1. Project Android Tidak Bisa Di-build

**Masalah:**
File [`RiskCalcApp.kt`](file:///D:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/ui/RiskCalcApp.kt) baris 33 mengimpor:
```kotlin
import com.riskcalc.mobile.ui.viewmodel.RiskViewModel
```
Tapi folder `viewmodel/` dan file `RiskViewModel.kt` **tidak ada** di struktur project.

**Akibat:** Android Studio akan langsung menampilkan error `Unresolved reference: RiskViewModel` dan project **tidak bisa di-compile sama sekali**.

**Solusi:** File `RiskViewModel.kt` harus dibuat di:
```
app/src/main/java/com/riskcalc/mobile/ui/viewmodel/RiskViewModel.kt
```
Teman yang mengerjakan bagian ini kemungkinan lupa push file tersebut ke Git.

---

### 2. Docs/Explanation Tidak Masuk Git

**Masalah:**
Di file [`.gitignore`](file:///D:/Kampus/project/ai/RiskCalc/.gitignore) baris 31:
```
# Ignored learning notes/explanations
/docs/explanation/
```

**Akibat:** Seluruh folder `docs/explanation/` — termasuk 11 file panduan yang sudah dibuat — **tidak pernah dikirim ke repository Git**. Siapapun yang clone repo ini tidak akan mendapat satu pun file dokumentasi.

**Solusi:** Hapus atau komentari baris itu di `.gitignore`:
```
# /docs/explanation/    ← tambah tanda # di depan
```
Lalu:
```powershell
git add docs/explanation/
git commit -m "docs: add explanation guides"
git push
```

---

### 3. Model Dilatih dari Data Tiruan — Akurasi 99.5% Adalah Palsu

**Masalah:**
Di [`train.py`](file:///D:/Kampus/project/ai/RiskCalc/backend/ml/train.py), label `y` dibuat dari rumus linear yang SAMA dengan yang akan dipelajari model:
```python
# Rumus untuk buat label:
score = (0.04 * age + 0.03 * (systolic_bp - 100) + ...)
y = (score > threshold).astype(int)

# Lalu model Perceptron diminta belajar memisahkan data tersebut
model.fit(X_train_scaled, y_train)
```

**Akibat:** Model tidak belajar dari pola medis nyata. Dia hanya belajar mengulang rumus matematika yang dipakai untuk membuat datanya sendiri. Ibarat mengerjakan soal ujian yang soal dan jawabannya sama persis dengan latihan — tentu dapat nilai 100%.

Akurasi **99.5% ini tidak bisa diklaim sebagai kinerja model yang sesungguhnya** dalam laporan atau skripsi.

**Solusi:** Ganti dataset dengan data rekam medis nyata (lihat `dataset_and_retrain_guide.md`). Target akurasi realistis dari dataset nyata adalah **70%–85%**.

---

## 🟡 PENTING — Sebaiknya Diperbaiki

### 4. `@app.on_event("startup")` Sudah Deprecated

**Masalah:**
Di [`main.py`](file:///D:/Kampus/project/ai/RiskCalc/backend/main.py) baris 28:
```python
@app.on_event("startup")  # ← Sudah deprecated sejak FastAPI 0.93
def load_ml_assets():
    ...
```

FastAPI versi terbaru sudah mengganti cara ini dengan pola `lifespan` yang lebih modern.

**Akibat:** Tidak langsung error, tapi akan muncul warning di terminal dan bisa tidak berfungsi di versi FastAPI yang lebih baru.

**Solusi — ganti dengan:**
```python
from contextlib import asynccontextmanager

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Kode yang jalan saat startup
    global model, scaler
    if os.path.exists(MODEL_PATH) and os.path.exists(SCALER_PATH):
        model = joblib.load(MODEL_PATH)
        scaler = joblib.load(SCALER_PATH)
        print("Model dan Scaler sukses dimuat!")
    yield
    # Kode yang jalan saat shutdown (opsional)

app = FastAPI(title="RiskCalc API", version="1.0", lifespan=lifespan)
```

---

### 5. Confidence Score Bukan Probabilitas Nyata

**Masalah:**
Di [`main.py`](file:///D:/Kampus/project/ai/RiskCalc/backend/main.py) baris 85:
```python
prob = 1 / (1 + np.exp(-abs(decision_val)))
```

Nilai ini bukan probabilitas yang dikalibrasi secara statistik. Nilai 0.87 di sini **tidak berarti** "model 87% yakin pasien ini berisiko tinggi" dalam arti probabilistik yang sesungguhnya.

**Akibat:** Jika diklaim sebagai "probabilitas" di laporan, bisa menjadi kelemahan yang ditanyakan penguji.

**Solusi:** Ganti label di response menjadi `"confidence_score"` bukan `"confidence"`, dan jelaskan di laporan bahwa ini adalah *pseudo-confidence based on distance to decision boundary*, bukan probabilitas Bayesian.

---

### 6. Tidak Ada Validasi Input di Sisi Android

**Masalah:**
Saat pengguna mengosongkan field "Usia" lalu langsung tekan Prediksi, app akan mengirim string kosong ke API dan API akan mengembalikan error 422. Pengalaman pengguna menjadi buruk.

**Solusi:** Tambahkan validasi di `RiskViewModel.kt` sebelum memanggil API:
```kotlin
fun predictRisk() {
    val ageInt = age.toIntOrNull()
    if (ageInt == null || ageInt < 1 || ageInt > 120) {
        resultText = "⚠️ Usia tidak valid (1-120 tahun)"
        return
    }
    // lanjut panggil API
}
```

---

### 7. Field Input Tidak Menggunakan Keyboard Numerik

**Masalah:**
Di [`RiskCalcApp.kt`](file:///D:/Kampus/project/ai/RiskCalc/app/src/main/java/com/riskcalc/mobile/ui/RiskCalcApp.kt), `OutlinedTextField` untuk usia, sistolik, dan kolesterol tidak memaksa keyboard angka:
```kotlin
OutlinedTextField(
    value = viewModel.age,
    // ← Tidak ada keyboardOptions!
)
```

**Akibat:** Di HP, pengguna akan mendapat keyboard huruf biasa saat mengisi usia, bukan keyboard angka. Sangat tidak nyaman.

**Solusi:**
```kotlin
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

OutlinedTextField(
    value = viewModel.age,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    ...
)
```

---

### 8. Backend Masih Monolitik — Tidak Sesuai Rencana di `agents.md`

**Masalah:**
`agents.md` merencanakan struktur folder yang rapi:
```
backend/
├── routers/predict.py
├── schemas/request.py
├── schemas/response.py
└── services/predictor.py
```

Tapi implementasi aktualnya: **semua ada dalam satu file `main.py`** (102 baris).

**Akibat:** Bukan masalah besar untuk proyek skala ini, tapi tidak konsisten dengan dokumentasi rencana. Jika ditanyakan penguji kenapa strukturnya berbeda dari rencana, harus bisa menjelaskan.

**Solusi:** Pilih salah satu — refactor sesuai rencana, atau update `agents.md` agar sesuai implementasi aktual.

---

## 🟢 MINOR — Opsional, Tapi Baik Diperbaiki

### 9. Tidak Ada Timeout di Retrofit

**Masalah:**
Jika server FastAPI lambat atau tidak merespons, Retrofit akan menunggu tanpa batas waktu. App akan tampak "freeze".

**Solusi — tambahkan timeout di `ApiClient.kt`:**
```kotlin
val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()
```

---

### 10. Release Build Tidak Di-Minify

**Masalah:**
Di [`app/build.gradle.kts`](file:///D:/Kampus/project/ai/RiskCalc/app/build.gradle.kts) baris 26:
```kotlin
isMinifyEnabled = false
```

**Akibat:** Kode Kotlin tidak di-obfuscate dan tidak dikompres untuk release. Ukuran APK lebih besar dan kode mudah di-reverse engineer.

**Untuk proyek skripsi ini tidak kritis**, tapi jika APK akan dibagikan ke luar, sebaiknya diaktifkan.

---

### 11. File `.pkl` Tidak Di-gitignore

**Masalah:**
File `model.pkl` dan `scaler.pkl` ikut dicommit ke Git. Untuk data sintetis ini tidak masalah, **tapi kalau nanti model diretrain dengan data pasien nyata**, bobot model bisa secara tidak langsung mengandung informasi pasien yang bersifat privasi.

**Solusi (untuk jaga-jaga):** Tambahkan ke `.gitignore`:
```
# ML Artifacts (dihasilkan oleh train.py)
backend/ml/artifacts/*.pkl
```
Kemudian dokumentasikan cara generate-nya di README.

---

## Ringkasan Prioritas

| No | Masalah | Tingkat | Status |
|----|---------|---------|--------|
| 1 | `RiskViewModel.kt` tidak ada → project tidak bisa build | 🔴 KRITIS | Harus diperbaiki |
| 2 | `docs/explanation/` tidak masuk Git | 🔴 KRITIS | Harus diperbaiki |
| 3 | Dataset sintetis → akurasi 99.5% palsu | 🔴 KRITIS | Harus diperbaiki sebelum sidang |
| 4 | `@app.on_event` deprecated | 🟡 PENTING | Sebaiknya diperbaiki |
| 5 | Confidence score bukan probabilitas nyata | 🟡 PENTING | Minimal harus dijelaskan di laporan |
| 6 | Tidak ada validasi input di Android | 🟡 PENTING | Diperbaiki sebelum demo |
| 7 | Keyboard input tidak numerik | 🟡 PENTING | Mudah diperbaiki, 1 baris kode |
| 8 | Struktur backend tidak sesuai `agents.md` | 🟡 PENTING | Pilih: refactor atau update docs |
| 9 | Tidak ada timeout Retrofit | 🟢 MINOR | Opsional |
| 10 | Release build tidak diminify | 🟢 MINOR | Opsional untuk skripsi |
| 11 | `.pkl` tidak di-gitignore | 🟢 MINOR | Penting jika nanti pakai data nyata |
