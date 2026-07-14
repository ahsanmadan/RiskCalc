package com.riskcalc.mobile.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.riskcalc.mobile.data.local.LocalRiskEngine
import com.riskcalc.mobile.data.repository.RiskRepository
import com.riskcalc.mobile.domain.model.RiskInput
import com.riskcalc.mobile.domain.model.RiskResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AssessmentStep {
    Age,
    Smoking,
    Systolic,
    Cholesterol;

    val number: Int get() = ordinal + 1
}

sealed interface RiskEvent {
    data object StartAssessment : RiskEvent
    data class AgeChanged(val value: String) : RiskEvent
    data class SmokingChanged(val value: Boolean) : RiskEvent
    data class SystolicChanged(val value: String) : RiskEvent
    data class CholesterolChanged(val value: String) : RiskEvent
    data object ContinueAssessment : RiskEvent
    data object GoBack : RiskEvent
    data object EditData : RiskEvent
    data object StartNewAssessment : RiskEvent
}

sealed interface RiskViewAction {
    data object None : RiskViewAction
    data object OpenAssessment : RiskViewAction
    data object OpenResult : RiskViewAction
    data object CloseAssessment : RiskViewAction
    data object BackToAssessment : RiskViewAction
}

@Immutable
data class RiskUiState(
    val currentStep: AssessmentStep = AssessmentStep.Age,
    val ageInput: String = "",
    val smokingInput: Boolean? = null,
    val systolicInput: String = "",
    val cholesterolInput: String = "",
    val errorMessage: String? = null,
    val result: RiskResult? = null,
    val modelError: String? = null
) {
    val isFirstStep: Boolean get() = currentStep == AssessmentStep.Age
    val isLastStep: Boolean get() = currentStep == AssessmentStep.Cholesterol
}

class RiskViewModel(
    private val savedStateHandle: SavedStateHandle,
    repositoryResult: Result<RiskRepository>
) : ViewModel() {
    private val repository = repositoryResult.getOrNull()
    private val initialState = RiskUiState(
        currentStep = AssessmentStep.entries[
            savedStateHandle[KEY_STEP] ?: AssessmentStep.Age.ordinal
        ],
        ageInput = savedStateHandle[KEY_AGE] ?: "",
        smokingInput = savedStateHandle[KEY_SMOKING],
        systolicInput = savedStateHandle[KEY_SYSTOLIC] ?: "",
        cholesterolInput = savedStateHandle[KEY_CHOLESTEROL] ?: "",
        modelError = repositoryResult.exceptionOrNull()?.let {
            "Model lokal belum dapat dimuat. Silakan pasang ulang aplikasi."
        }
    )
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<RiskUiState> = _uiState.asStateFlow()

    init {
        calculateIfComplete()
    }

    fun onEvent(event: RiskEvent): RiskViewAction {
        return when (event) {
            RiskEvent.StartAssessment -> {
                resetForNewAssessment()
                RiskViewAction.OpenAssessment
            }
            is RiskEvent.AgeChanged -> {
                updateInput(KEY_AGE, event.value) { copy(ageInput = event.value) }
                RiskViewAction.None
            }
            is RiskEvent.SmokingChanged -> {
                savedStateHandle[KEY_SMOKING] = event.value
                _uiState.update {
                    it.copy(
                        smokingInput = event.value,
                        errorMessage = null,
                        result = null
                    )
                }
                RiskViewAction.None
            }
            is RiskEvent.SystolicChanged -> {
                updateInput(KEY_SYSTOLIC, event.value) { copy(systolicInput = event.value) }
                RiskViewAction.None
            }
            is RiskEvent.CholesterolChanged -> {
                updateInput(KEY_CHOLESTEROL, event.value) { copy(cholesterolInput = event.value) }
                RiskViewAction.None
            }
            RiskEvent.ContinueAssessment -> continueAssessment()
            RiskEvent.GoBack -> goBack()
            RiskEvent.EditData -> {
                setStep(AssessmentStep.Age)
                RiskViewAction.BackToAssessment
            }
            RiskEvent.StartNewAssessment -> {
                resetForNewAssessment()
                RiskViewAction.CloseAssessment
            }
        }
    }

    private fun continueAssessment(): RiskViewAction {
        val error = validationError(_uiState.value.currentStep)
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return RiskViewAction.None
        }

        val current = _uiState.value.currentStep
        if (current == AssessmentStep.Cholesterol) {
            return if (calculate()) RiskViewAction.OpenResult else RiskViewAction.None
        }

        setStep(AssessmentStep.entries[current.ordinal + 1])
        return RiskViewAction.None
    }

    private fun goBack(): RiskViewAction {
        val current = _uiState.value.currentStep
        if (current == AssessmentStep.Age) return RiskViewAction.CloseAssessment

        setStep(AssessmentStep.entries[current.ordinal - 1])
        return RiskViewAction.None
    }

    private fun calculate(): Boolean {
        val input = parsedInput() ?: return false
        val localRepository = repository ?: return false
        val result = localRepository.calculate(input)
        _uiState.update { it.copy(result = result, errorMessage = null) }
        return true
    }

    private fun calculateIfComplete() {
        if (parsedInput() != null && repository != null) {
            calculate()
        }
    }

    private fun parsedInput(): RiskInput? {
        val state = _uiState.value
        return RiskInput(
            age = state.ageInput.toIntOrNull() ?: return null,
            systolicBp = state.systolicInput.normalizeDecimal().toDoubleOrNull() ?: return null,
            totalCholesterol = state.cholesterolInput.toIntOrNull() ?: return null,
            isCurrentSmoker = state.smokingInput ?: return null
        )
    }

    private fun validationError(step: AssessmentStep): String? {
        val state = _uiState.value
        return when (step) {
            AssessmentStep.Age -> when (state.ageInput.toIntOrNull()) {
                null -> "Masukkan usia dalam angka."
                !in AGE_RANGE -> "Model hanya mendukung usia 32-70 tahun."
                else -> null
            }
            AssessmentStep.Smoking -> if (state.smokingInput == null) {
                "Pilih Ya atau Tidak untuk melanjutkan."
            } else {
                null
            }
            AssessmentStep.Systolic -> when (
                val value = state.systolicInput.normalizeDecimal().toDoubleOrNull()
            ) {
                null -> "Masukkan tekanan sistolik dalam angka."
                else -> if (value !in SYSTOLIC_RANGE) {
                    "Model hanya mendukung sistolik 90-220 mmHg."
                } else {
                    null
                }
            }
            AssessmentStep.Cholesterol -> when (state.cholesterolInput.toIntOrNull()) {
                null -> "Masukkan kolesterol total dalam angka bulat."
                !in CHOLESTEROL_RANGE -> "Model hanya mendukung kolesterol 124-398 mg/dL."
                else -> null
            }
        }
    }

    private fun setStep(step: AssessmentStep) {
        savedStateHandle[KEY_STEP] = step.ordinal
        _uiState.update { it.copy(currentStep = step, errorMessage = null) }
    }

    private fun resetForNewAssessment() {
        savedStateHandle.remove<String>(KEY_AGE)
        savedStateHandle.remove<Boolean>(KEY_SMOKING)
        savedStateHandle.remove<String>(KEY_SYSTOLIC)
        savedStateHandle.remove<String>(KEY_CHOLESTEROL)
        savedStateHandle[KEY_STEP] = AssessmentStep.Age.ordinal
        _uiState.value = RiskUiState(modelError = _uiState.value.modelError)
    }

    private fun updateInput(
        key: String,
        value: String,
        transform: RiskUiState.() -> RiskUiState
    ) {
        savedStateHandle[key] = value
        _uiState.update { it.transform().copy(errorMessage = null, result = null) }
    }

    companion object {
        val AGE_RANGE = 32..70
        val SYSTOLIC_RANGE = 90.0..220.0
        val CHOLESTEROL_RANGE = 124..398
        private const val KEY_STEP = "assessment_step"
        private const val KEY_AGE = "age_input"
        private const val KEY_SMOKING = "smoking_input"
        private const val KEY_SYSTOLIC = "systolic_input"
        private const val KEY_CHOLESTEROL = "cholesterol_input"

        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = runCatching {
                    RiskRepository(LocalRiskEngine.fromAssets(context.assets))
                }
                RiskViewModel(createSavedStateHandle(), repository)
            }
        }
    }
}

private fun String.normalizeDecimal(): String = replace(',', '.')
