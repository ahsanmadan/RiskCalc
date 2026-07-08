package com.riskcalc.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PredictRequest(
    @SerializedName("age") val age: Int,
    @SerializedName("systolic_bp") val systolicBp: Int,
    @SerializedName("total_cholesterol") val totalCholesterol: Double,
    @SerializedName("smoking_status") val smokingStatus: Int
)
