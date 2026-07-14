package com.riskcalc.mobile.data.repository

import com.riskcalc.mobile.data.local.LocalRiskEngine
import com.riskcalc.mobile.domain.RiskEducationBuilder
import com.riskcalc.mobile.domain.model.RiskInput
import com.riskcalc.mobile.domain.model.RiskResult

class RiskRepository(
    private val engine: LocalRiskEngine
) {
    fun calculate(input: RiskInput): RiskResult {
        val prediction = engine.predict(input)
        return RiskEducationBuilder.build(input, prediction)
    }
}
