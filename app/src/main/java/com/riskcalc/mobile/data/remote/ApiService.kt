package com.riskcalc.mobile.data.remote

import com.riskcalc.mobile.data.remote.dto.PredictRequest
import com.riskcalc.mobile.data.remote.dto.PredictResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("predict")
    suspend fun getPrediction(
        @Body request: PredictRequest
    ): PredictResponse
}
