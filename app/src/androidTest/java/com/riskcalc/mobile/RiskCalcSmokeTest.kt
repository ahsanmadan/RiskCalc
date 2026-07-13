package com.riskcalc.mobile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RiskCalcSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun cold_start_shows_intro_and_disclaimer() {
        composeRule.onNodeWithText("RiskCalc").assertIsDisplayed()
        composeRule.onNodeWithText("Mulai periksa").assertIsDisplayed()
        composeRule.onNodeWithText(
            "Hasil ini hanya simulasi skrining awal untuk pembelajaran, bukan diagnosis medis atau pengganti pemeriksaan tenaga kesehatan."
        ).assertIsDisplayed()
    }

    @Test
    fun severe_systolic_warning_appears_before_result() {
        composeRule.onNodeWithText("Mulai periksa").performClick()
        enterCurrentNumber("60")
        composeRule.onNodeWithText("Lanjut").performClick()
        composeRule.onNodeWithText("Ya, saya merokok").performClick()
        composeRule.onNodeWithText("Lanjut").performClick()
        enterCurrentNumber("181")
        composeRule.onNodeWithText("Lanjut").performClick()
        enterCurrentNumber("240")
        composeRule.onNodeWithText("Lihat hasil").performClick()

        composeRule.onNodeWithText("Tekanan sistolik perlu perhatian segera").assertIsDisplayed()
        assertEquals(
            0,
            composeRule.onAllNodesWithText("Hasil simulasi").fetchSemanticsNodes().size
        )
        composeRule.onNodeWithText("Saya mengerti, lanjutkan").performClick()
        composeRule.onNodeWithText("Hasil simulasi").assertIsDisplayed()
    }

    private fun enterCurrentNumber(value: String) {
        composeRule.onNode(hasSetTextAction()).performTextInput(value)
    }
}
