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
        
        return PredictResponse(
            prediction=prediction,
            label=label,
            confidence=round(float(prob), 4)
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Terjadi kesalahan saat memproses prediksi: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
