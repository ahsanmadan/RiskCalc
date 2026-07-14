package com.riskcalc.mobile.data.local

import android.content.res.AssetManager
import com.riskcalc.mobile.domain.model.RiskInput
import com.riskcalc.mobile.domain.model.RiskPrediction
import kotlinx.serialization.json.Json
import kotlin.math.abs

class LocalRiskEngine(
    private val artifact: RiskModelArtifact
) {
    init {
        require(artifact.schemaVersion == 1) { "Versi artefak model tidak didukung." }
        require(artifact.featureOrder == EXPECTED_FEATURE_ORDER) { "Urutan fitur model tidak sesuai." }
        require(artifact.means.size == FEATURE_COUNT)
        require(artifact.scales.size == FEATURE_COUNT)
        require(artifact.weights.size == FEATURE_COUNT)
        require(artifact.scales.none { it == 0.0 }) { "Skala model tidak valid." }
    }

    fun predict(input: RiskInput): RiskPrediction {
        val rawFeatures = doubleArrayOf(
            input.age.toDouble(),
            input.systolicBp,
            input.totalCholesterol.toDouble(),
            if (input.isCurrentSmoker) 1.0 else 0.0
        )
        var margin = artifact.intercept
        for (index in rawFeatures.indices) {
            val standardized = (rawFeatures[index] - artifact.means[index]) / artifact.scales[index]
            margin += artifact.weights[index] * standardized
        }
        val classId = if (margin >= 0.0) 1 else 0
        return RiskPrediction(
            classId = classId,
            displayLabel = if (classId == 1) "Risiko lebih tinggi" else "Risiko lebih rendah",
            decisionMargin = margin,
            isBorderline = abs(margin) <= artifact.borderlineMargin
        )
    }

    companion object {
        private const val FEATURE_COUNT = 4
        private val EXPECTED_FEATURE_ORDER = listOf(
            "age",
            "systolic_bp",
            "total_cholesterol",
            "smoking"
        )
        private val json = Json { ignoreUnknownKeys = false }

        fun fromAssets(assetManager: AssetManager): LocalRiskEngine {
            val modelJson = assetManager.open("risk_model.json")
                .bufferedReader()
                .use { it.readText() }
            return LocalRiskEngine(json.decodeFromString<RiskModelArtifact>(modelJson))
        }
    }
}
