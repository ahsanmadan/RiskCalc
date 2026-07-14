package com.riskcalc.mobile.domain

import com.riskcalc.mobile.domain.model.FactorTone
import com.riskcalc.mobile.domain.model.RiskInput
import com.riskcalc.mobile.domain.model.RiskPrediction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RiskEducationBuilderTest {
    @Test
    fun systolic_above_180_creates_urgent_guardrail() {
        val result = RiskEducationBuilder.build(
            input = RiskInput(60, 181.0, 240, true),
            prediction = RiskPrediction(1, "Risiko lebih tinggi", 1.0, false)
        )

        assertTrue(result.severeBloodPressureWarning)
        assertEquals(FactorTone.Urgent, result.factors.first { it.title == "Tekanan darah" }.tone)
    }

    @Test
    fun tips_are_relevant_unique_and_limited_to_five() {
        val result = RiskEducationBuilder.build(
            input = RiskInput(60, 150.0, 260, true),
            prediction = RiskPrediction(1, "Risiko lebih tinggi", 1.0, false)
        )

        assertTrue(result.tips.size in 3..5)
        assertEquals(result.tips.distinct(), result.tips)
        assertTrue(result.tips.any { it.contains("berhenti merokok") })
    }
}
