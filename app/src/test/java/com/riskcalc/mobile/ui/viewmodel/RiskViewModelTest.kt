package com.riskcalc.mobile.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.riskcalc.mobile.data.local.LocalRiskEngine
import com.riskcalc.mobile.data.local.RiskModelArtifact
import com.riskcalc.mobile.data.repository.RiskRepository
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class RiskViewModelTest {
    @Test
    fun stepper_validates_ranges_and_accepts_comma_decimal() {
        val viewModel = createViewModel()

        viewModel.updateAge("31")
        assertFalse(viewModel.continueStep())
        assertEquals("Model hanya mendukung usia 32-70 tahun.", viewModel.uiState.value.errorMessage)

        viewModel.updateAge("45")
        assertFalse(viewModel.continueStep())
        assertEquals(AssessmentStep.Smoking, viewModel.uiState.value.currentStep)

        assertFalse(viewModel.continueStep())
        assertNotNull(viewModel.uiState.value.errorMessage)
        viewModel.updateSmoking(false)
        viewModel.continueStep()

        viewModel.updateSystolic("120,5")
        viewModel.continueStep()
        viewModel.updateCholesterol("200")

        assertTrue(viewModel.continueStep())
        assertNotNull(viewModel.uiState.value.result)
        assertEquals(120.5, viewModel.uiState.value.result?.input?.systolicBp ?: 0.0, 0.0)
    }

    @Test
    fun reset_removes_session_inputs_and_result() {
        val viewModel = createViewModel()
        viewModel.updateAge("50")
        viewModel.continueStep()
        viewModel.updateSmoking(true)
        viewModel.continueStep()
        viewModel.updateSystolic("140")
        viewModel.continueStep()
        viewModel.updateCholesterol("240")
        assertTrue(viewModel.continueStep())

        viewModel.startNewAssessment()

        val state = viewModel.uiState.value
        assertEquals(AssessmentStep.Age, state.currentStep)
        assertEquals("", state.ageInput)
        assertNull(state.smokingInput)
        assertEquals("", state.systolicInput)
        assertEquals("", state.cholesterolInput)
        assertNull(state.result)
    }

    private fun createViewModel(): RiskViewModel {
        val artifact = Json.decodeFromString<RiskModelArtifact>(
            File("src/main/assets/risk_model.json").readText()
        )
        return RiskViewModel(
            savedStateHandle = SavedStateHandle(),
            repositoryResult = Result.success(RiskRepository(LocalRiskEngine(artifact)))
        )
    }
}
