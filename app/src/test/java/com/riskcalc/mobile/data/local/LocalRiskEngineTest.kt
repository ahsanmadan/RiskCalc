package com.riskcalc.mobile.data.local

import com.riskcalc.mobile.domain.model.RiskInput
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LocalRiskEngineTest {
    private val json = Json
    private val artifact = json.decodeFromString<RiskModelArtifact>(
        File("src/main/assets/risk_model.json").readText()
    )
    private val engine = LocalRiskEngine(artifact)

    @Test
    fun kotlin_prediction_matches_all_python_parity_cases() {
        val fixtureText = requireNotNull(
            javaClass.getResourceAsStream("/parity_cases.json")
        ).bufferedReader().use { it.readText() }
        val fixture = json.decodeFromString<ParityFixture>(fixtureText)

        assertEquals(4159, fixture.cases.size)
        fixture.cases.forEachIndexed { index, case ->
            val result = engine.predict(case.toRiskInput())
            assertEquals("Class mismatch at row $index", case.expectedClass, result.classId)
            assertEquals(
                "Margin mismatch at row $index",
                case.expectedMargin,
                result.decisionMargin,
                1e-9
            )
        }
    }

    @Test
    fun prediction_marks_margin_inside_exported_threshold_as_borderline() {
        val fixtureText = requireNotNull(
            javaClass.getResourceAsStream("/parity_cases.json")
        ).bufferedReader().use { it.readText() }
        val fixture = json.decodeFromString<ParityFixture>(fixtureText)
        val closest = fixture.cases.minBy { kotlin.math.abs(it.expectedMargin) }
        val farthest = fixture.cases.maxBy { kotlin.math.abs(it.expectedMargin) }

        assertTrue(engine.predict(closest.toRiskInput()).isBorderline)
        assertFalse(engine.predict(farthest.toRiskInput()).isBorderline)
    }
}

@Serializable
private data class ParityFixture(
    val schemaVersion: Int,
    val cases: List<ParityCase>
)

@Serializable
private data class ParityCase(
    val age: Int,
    val systolicBp: Double,
    val totalCholesterol: Int,
    val isCurrentSmoker: Boolean,
    val expectedClass: Int,
    val expectedMargin: Double
) {
    fun toRiskInput() = RiskInput(
        age = age,
        systolicBp = systolicBp,
        totalCholesterol = totalCholesterol,
        isCurrentSmoker = isCurrentSmoker
    )
}
