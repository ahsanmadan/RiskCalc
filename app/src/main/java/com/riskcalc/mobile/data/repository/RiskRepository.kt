package com.riskcalc.mobile.data.repository

import com.riskcalc.mobile.data.remote.ApiClient
import com.riskcalc.mobile.data.remote.dto.PredictRequest
import com.riskcalc.mobile.data.remote.dto.PredictResponse

class RiskRepository {
    private val apiService = ApiClient.apiService

    suspend fun getPrediction(
        age: Int,
        systolicBp: Int,
        totalCholesterol: Double,
        smokingStatus: Int
    ): PredictResponse {
        val request = PredictRequest(
            age = age,
            systolicBp = systolicBp,
            totalCholesterol = totalCholesterol,
            smokingStatus = smokingStatus
        )
        return apiService.getPrediction(request)
    }
}
