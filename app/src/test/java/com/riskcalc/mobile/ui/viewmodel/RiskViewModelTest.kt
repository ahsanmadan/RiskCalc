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

    @Test
    fun minimum_and_maximum_training_boundaries_are_accepted() {
        listOf(
            Triple("32", "90", "124"),
            Triple("70", "220", "398")
        ).forEach { (age, systolic, cholesterol) ->
            val viewModel = createViewModel()
            completeAssessment(viewModel, age, false, systolic, cholesterol)
            assertNotNull(viewModel.uiState.value.result)
        }
    }

    @Test
    fun malformed_empty_and_out_of_range_values_are_blocked() {
        val ageViewModel = createViewModel()
        listOf("", "usia", "31", "71", "32.5").forEach { value ->
            ageViewModel.updateAge(value)
            assertFalse(ageViewModel.continueStep())
            assertEquals(AssessmentStep.Age, ageViewModel.uiState.value.currentStep)
        }

        val systolicViewModel = createViewModel()
        systolicViewModel.updateAge("45")
        systolicViewModel.continueStep()
        systolicViewModel.updateSmoking(false)
        systolicViewModel.continueStep()
        listOf("", "abc", "89,9", "220.1").forEach { value ->
            systolicViewModel.updateSystolic(value)
            assertFalse(systolicViewModel.continueStep())
            assertEquals(AssessmentStep.Systolic, systolicViewModel.uiState.value.currentStep)
        }

        val cholesterolViewModel = createViewModel()
        cholesterolViewModel.updateAge("45")
        cholesterolViewModel.continueStep()
        cholesterolViewModel.updateSmoking(false)
        cholesterolViewModel.continueStep()
        cholesterolViewModel.updateSystolic("120.5")
        cholesterolViewModel.continueStep()
        listOf("", "abc", "123", "399", "200.5").forEach { value ->
            cholesterolViewModel.updateCholesterol(value)
            assertFalse(cholesterolViewModel.continueStep())
            assertEquals(AssessmentStep.Cholesterol, cholesterolViewModel.uiState.value.currentStep)
        }
    }

    @Test
    fun back_edit_and_saved_state_keep_inputs() {
        val handle = SavedStateHandle()
        val viewModel = createViewModel(handle)
        completeAssessment(viewModel, "50", true, "181,5", "240")

        viewModel.editData()
        assertEquals(AssessmentStep.Age, viewModel.uiState.value.currentStep)
        assertEquals("50", viewModel.uiState.value.ageInput)
        assertEquals(true, viewModel.uiState.value.smokingInput)
        assertEquals("181,5", viewModel.uiState.value.systolicInput)
        assertEquals("240", viewModel.uiState.value.cholesterolInput)

        viewModel.continueStep()
        assertTrue(viewModel.previousStep())
        assertEquals(AssessmentStep.Age, viewModel.uiState.value.currentStep)

        val recreated = createViewModel(handle)
        assertEquals("50", recreated.uiState.value.ageInput)
        assertEquals(true, recreated.uiState.value.smokingInput)
        assertNotNull(recreated.uiState.value.result)
    }

    @Test
    fun fresh_session_starts_without_previous_health_data() {
        val firstSession = createViewModel()
        completeAssessment(firstSession, "50", true, "140", "240")

        val freshSession = createViewModel()
        assertEquals(RiskUiState(), freshSession.uiState.value)
    }

    private fun completeAssessment(
        viewModel: RiskViewModel,
        age: String,
        smoking: Boolean,
        systolic: String,
        cholesterol: String
    ) {
        viewModel.updateAge(age)
        viewModel.continueStep()
        viewModel.updateSmoking(smoking)
        viewModel.continueStep()
        viewModel.updateSystolic(systolic)
        viewModel.continueStep()
        viewModel.updateCholesterol(cholesterol)
        assertTrue(viewModel.continueStep())
    }

    private fun createViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()): RiskViewModel {
        val artifact = Json.decodeFromString<RiskModelArtifact>(
            File("src/main/assets/risk_model.json").readText()
        )
        return RiskViewModel(
            savedStateHandle = savedStateHandle,
            repositoryResult = Result.success(RiskRepository(LocalRiskEngine(artifact)))
        )
    }
}
