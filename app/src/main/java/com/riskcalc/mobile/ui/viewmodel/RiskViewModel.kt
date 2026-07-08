package com.riskcalc.mobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riskcalc.mobile.data.repository.RiskRepository
import kotlinx.coroutines.launch
import java.io.IOException

class RiskViewModel(
    private val repository: RiskRepository = RiskRepository()
) : ViewModel() {

    // State untuk Input Form
    var age by mutableStateOf("")
        private set

    var systolicBp by mutableStateOf("")
        private set

    var totalCholesterol by mutableStateOf("")
        private set

    var isSmoker by mutableStateOf(false)
        private set

    // State untuk Status API
    var isLoading by mutableStateOf(false)
        private set

    var resultText by mutableStateOf("Hasil prediksi akan tampil di sini.")
        private set

    var predictionClass by mutableStateOf<Int?>(null)
        private set

    var confidence by mutableStateOf<Double?>(null)
        private set

    // Setter functions untuk Form Input
    fun onAgeChange(value: String) {
        // Hanya izinkan angka
        if (value.isEmpty() || value.all { it.isDigit() }) {
            age = value
        }
    }

    fun onSystolicBpChange(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            systolicBp = value
        }
    }

    fun onTotalCholesterolChange(value: String) {
        // Izinkan angka desimal titik tunggal
        if (value.isEmpty() || value.count { it == '.' } <= 1 && value.all { it.isDigit() || it == '.' }) {
            totalCholesterol = value
        }
    }

    fun onSmokerChange(value: Boolean) {
        isSmoker = value
    }

    // Fungsi utama memanggil API FastAPI
    fun predictRisk() {
        // 1. Validasi Input
        val ageVal = age.toIntOrNull()
        val bpVal = systolicBp.toIntOrNull()
        val cholVal = totalCholesterol.toDoubleOrNull()
        
        if (ageVal == null || bpVal == null || cholVal == null) {
            resultText = "Harap isi semua kolom input dengan angka yang valid!"
            predictionClass = null
            confidence = null
            return
        }

        // 2. Set loading state
        isLoading = true
        resultText = "Sedang menghubungi server..."
        predictionClass = null
        confidence = null

        // 3. Jalankan Coroutine di latar belakang
        viewModelScope.launch {
            try {
                val smokingStatus = if (isSmoker) 1 else 0
                val response = repository.getPrediction(
                    age = ageVal,
                    systolicBp = bpVal,
                    totalCholesterol = cholVal,
                    smokingStatus = smokingStatus
                )
                
                // 4. Sukses: Update state dengan hasil prediksi
                predictionClass = response.prediction
                confidence = response.confidence
                resultText = "Pasien diklasifikasikan dengan ${response.label} (Tingkat keyakinan: ${(response.confidence * 100).toInt()}%)"
            } catch (e: IOException) {
                // Error koneksi internet/server mati
                resultText = "Koneksi ke API gagal. Pastikan server FastAPI sudah berjalan dan jalankan perintah 'adb reverse tcp:8000 tcp:8000' lewat kabel USB."
                predictionClass = null
                confidence = null
            } catch (e: Exception) {
                // Error lainnya (HTTP error, parsing error)
                resultText = "Terjadi kesalahan sistem: ${e.localizedMessage ?: "Unknown Error"}"
                predictionClass = null
                confidence = null
            } finally {
                isLoading = false
            }
        }
    }
}
