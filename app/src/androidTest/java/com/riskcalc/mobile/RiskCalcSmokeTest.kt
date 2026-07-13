package com.riskcalc.mobile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
}
