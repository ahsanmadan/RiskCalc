import os
import numpy as np
from sklearn.linear_model import Perceptron
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
import joblib

def main():
    print("Mulai pembuatan dataset tiruan (synthetic dataset)...")
    
    # Set seed agar repeatable
    np.random.seed(42)
    
    # Generate 1000 data sampel
    n_samples = 1000
    
    # Generate features
    age = np.random.randint(20, 80, size=n_samples)
    systolic_bp = np.random.randint(90, 180, size=n_samples)
    total_cholesterol = np.random.uniform(120, 320, size=n_samples)
    smoking_status = np.random.choice([0, 1], size=n_samples, p=[0.7, 0.3])
    
    X = np.stack([age, systolic_bp, total_cholesterol, smoking_status], axis=1)
    
    # Tentukan label menggunakan rumus linear (linear decision boundary)
    # agar model Perceptron (classifier linier) bisa mempelajari dengan baik.
    # Faktor risiko bertambah seiring bertambahnya usia, tekanan darah, kolesterol, dan merokok.
    score = (
        0.04 * age + 
        0.03 * (systolic_bp - 100) + 
        0.015 * (total_cholesterol - 150) + 
        0.8 * smoking_status
    )
    
    # Median score sebagai threshold pembagi kelas 0 dan 1
    threshold = np.median(score)
    y = (score > threshold).astype(int)
    
    # Split dataset
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    # Normalisasi data menggunakan StandardScaler
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)
    
    # Training Model Perceptron
    print("Melatih model Perceptron...")
    model = Perceptron(max_iter=1000, tol=1e-3, random_state=42)
    model.fit(X_train_scaled, y_train)
    
    # Evaluasi Model
    y_pred = model.predict(X_test_scaled)
    acc = accuracy_score(y_test, y_pred)
    print(f"Training Selesai. Akurasi Model pada Test Set: {acc * 100:.2f}%")
    
    # Pastikan direktori artifacts ada
    artifacts_dir = os.path.join(os.path.dirname(__file__), "artifacts")
    os.makedirs(artifacts_dir, exist_ok=True)
    
    # Simpan model.pkl dan scaler.pkl
    model_path = os.path.join(artifacts_dir, "model.pkl")
    scaler_path = os.path.join(artifacts_dir, "scaler.pkl")
    
    joblib.dump(model, model_path)
    joblib.dump(scaler, scaler_path)
    
    print(f"Artefak model berhasil disimpan di:\n- {model_path}\n- {scaler_path}")

if __name__ == "__main__":
    main()
