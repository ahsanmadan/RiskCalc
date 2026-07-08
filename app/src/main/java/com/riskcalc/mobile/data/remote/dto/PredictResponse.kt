package com.riskcalc.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PredictResponse(
    @SerializedName("prediction") val prediction: Int,
    @SerializedName("label") val label: String,
    @SerializedName("confidence") val confidence: Double,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("recommendations") val recommendations: List<String>
)
