package com.riskcalc.mobile.data.local

import kotlinx.serialization.Serializable

@Serializable
data class RiskModelArtifact(
    val schemaVersion: Int,
    val modelVersion: String,
    val datasetSha256: String,
    val featureOrder: List<String>,
    val means: List<Double>,
    val scales: List<Double>,
    val weights: List<Double>,
    val intercept: Double,
    val classLabels: Map<String, String>,
    val borderlineMargin: Double
)
