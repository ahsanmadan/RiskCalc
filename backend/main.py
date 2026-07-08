import os
import numpy as np
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import joblib

app = FastAPI(title="RiskCalc API", version="1.0")

# Tambahkan CORS Middleware agar bisa diakses dari aplikasi Android
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Definisikan path ke file model dan scaler
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "ml", "artifacts", "model.pkl")
SCALER_PATH = os.path.join(BASE_DIR, "ml", "artifacts", "scaler.pkl")

model = None
scaler = None

# Load model dan scaler saat startup
@app.on_event("startup")
def load_ml_assets():
    global model, scaler
    try:
        if os.path.exists(MODEL_PATH) and os.path.exists(SCALER_PATH):
            model = joblib.load(MODEL_PATH)
            scaler = joblib.load(SCALER_PATH)
            print("Model dan Scaler sukses dimuat!")
        else:
            print("PERINGATAN: File model.pkl atau scaler.pkl tidak ditemukan. Silakan jalankan train.py terlebih dahulu.")
    except Exception as e:
        print(f"Error memuat model/scaler: {e}")

# Skema Request Body Pydantic
class PredictRequest(BaseModel):
    age: int = Field(..., ge=0, le=120, description="Usia pasien dalam tahun")
    systolic_bp: int = Field(..., ge=50, le=300, description="Tekanan darah sistolik dalam mmHg")
    total_cholesterol: float = Field(..., ge=50, le=600, description="Kolesterol total dalam mg/dL")
    smoking_status: int = Field(..., ge=0, le=1, description="Status merokok (0 = Tidak, 1 = Perokok)")

# Skema Response Body Pydantic
class PredictResponse(BaseModel):
    prediction: int = Field(..., description="0 = Risiko Rendah, 1 = Risiko Tinggi")
    label: str = Field(..., description="Teks klasifikasi risiko ('Risiko Rendah' atau 'Risiko Tinggi')")
    confidence: float = Field(..., description="Nilai tingkat keyakinan (confidence score)")
    explanation: str = Field(..., description="Penjelasan klinis mengenai tingkat risiko")
    recommendations: list[str] = Field(..., description="Daftar rekomendasi solusi medis")

@app.get("/health")
def health_check():
    return {
        "status": "ok",
        "model_loaded": (model is not None and scaler is not None)
    }

@app.post("/predict", response_model=PredictResponse)
def predict(request: PredictRequest):
    global model, scaler
    if model is None or scaler is None:
        raise HTTPException(status_code=503, detail="Model Machine Learning belum dimuat di server.")
    
    try:
        # 1. Siapkan fitur input sesuai dengan urutan saat training
        features = np.array([[
            request.age,
            request.systolic_bp,
            request.total_cholesterol,
            request.smoking_status
        ]])
        
        # 2. Lakukan standarisasi menggunakan scaler yang sudah di-fit
        features_scaled = scaler.transform(features)
        
        # 3. Lakukan prediksi kelas (0 atau 1)
        prediction = int(model.predict(features_scaled)[0])
        
        # 4. Hitung jarak ke decision boundary untuk mendapatkan pseudo-confidence
        decision_val = model.decision_function(features_scaled)[0]
        # Gunakan fungsi sigmoid untuk merubah nilai jarak menjadi skor probabilitas (0.0 sampai 1.0)
        prob = 1 / (1 + np.exp(-abs(decision_val)))
        
        # Format label hasil prediksi
        label = "Risiko Tinggi" if prediction == 1 else "Risiko Rendah"
        
        # Penjelasan klinis & rekomendasi penanggulangan dinamis
        if prediction == 1:
            explanation = "Berdasarkan analisis model Perceptron, Anda memiliki faktor risiko tinggi terhadap penyakit kardiovaskular. Hal ini dipengaruhi oleh kombinasi variabel klinis seperti usia, tekanan darah sistolik, kadar kolesterol total, dan status merokok Anda."
            recommendations = [
                "Segera lakukan konsultasi dengan dokter spesialis jantung atau dokter umum.",
                "Kurangi konsumsi makanan asin (natrium tinggi), makanan berlemak jenuh, dan makanan olahan.",
                "Berhenti merokok sepenuhnya dan hindari paparan asap rokok pasif di lingkungan Anda.",
                "Lakukan olahraga kardio intensitas ringan-sedang (seperti jalan cepat) minimal 30 menit sehari.",
                "Lakukan pemantauan mandiri terhadap tekanan darah dan cek profil lipid (kolesterol) secara rutin."
            ]
        else:
            explanation = "Berdasarkan analisis model Perceptron, Anda tergolong memiliki faktor risiko rendah terhadap penyakit kardiovaskular saat ini. Namun, mempertahankan pola hidup sehat sangat disarankan untuk pencegahan jangka panjang."
            recommendations = [
                "Pertahankan pola makan tinggi serat dengan memperbanyak konsumsi sayur, buah, dan biji-bijian.",
                "Lakukan aktivitas fisik secara teratur minimal 150 menit per minggu.",
                "Hindari memulai kebiasaan merokok dan konsumsi minuman beralkohol.",
                "Lakukan pemeriksaan kesehatan berkala (medical check-up) minimal satu tahun sekali.",
                "Kelola stres dengan baik dan pastikan mendapatkan istirahat yang cukup (7-8 jam per hari)."
            ]
        
        return PredictResponse(
            prediction=prediction,
            label=label,
            confidence=round(float(prob), 4),
            explanation=explanation,
            recommendations=recommendations
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Terjadi kesalahan saat memproses prediksi: {str(e)}")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
