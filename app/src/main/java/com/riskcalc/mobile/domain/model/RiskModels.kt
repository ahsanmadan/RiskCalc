package com.riskcalc.mobile.domain.model

data class RiskInput(
    val age: Int,
    val systolicBp: Double,
    val totalCholesterol: Int,
    val isCurrentSmoker: Boolean
)

data class RiskPrediction(
    val classId: Int,
    val displayLabel: String,
    val decisionMargin: Double,
    val isBorderline: Boolean
)

enum class FactorTone {
    Neutral,
    Positive,
    Attention,
    Urgent
}

data class FactorSummary(
    val title: String,
    val value: String,
    val description: String,
    val tone: FactorTone
)

data class RiskResult(
    val input: RiskInput,
    val prediction: RiskPrediction,
    val factors: List<FactorSummary>,
    val tips: List<String>,
    val severeBloodPressureWarning: Boolean
)
