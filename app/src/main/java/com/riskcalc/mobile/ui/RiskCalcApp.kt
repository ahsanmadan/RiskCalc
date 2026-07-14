package com.riskcalc.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.riskcalc.mobile.ui.navigation.AssessmentRoute
import com.riskcalc.mobile.ui.navigation.IntroRoute
import com.riskcalc.mobile.ui.navigation.ResultRoute
import com.riskcalc.mobile.ui.screens.AssessmentScreen
import com.riskcalc.mobile.ui.screens.IntroScreen
import com.riskcalc.mobile.ui.screens.ResultScreen
import com.riskcalc.mobile.ui.viewmodel.RiskEvent
import com.riskcalc.mobile.ui.viewmodel.RiskViewAction
import com.riskcalc.mobile.ui.viewmodel.RiskViewModel

@Composable
fun RiskCalcApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val factory = remember(context) { RiskViewModel.factory(context) }
    val viewModel: RiskViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(IntroRoute)

    fun handleAction(action: RiskViewAction) {
        when (action) {
            RiskViewAction.None -> Unit
            RiskViewAction.OpenAssessment -> backStack.add(AssessmentRoute)
            RiskViewAction.OpenResult -> backStack.add(ResultRoute)
            RiskViewAction.CloseAssessment -> backStack.removeLastOrNull()
            RiskViewAction.BackToAssessment -> backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { route ->
            when (route) {
                IntroRoute -> NavEntry(route) {
                    IntroScreen(
                        modelError = state.modelError,
                        onStart = {
                            handleAction(viewModel.onEvent(RiskEvent.StartAssessment))
                        }
                    )
                }
                AssessmentRoute -> NavEntry(route) {
                    AssessmentScreen(
                        state = state,
                        onAgeChange = { value ->
                            viewModel.onEvent(RiskEvent.AgeChanged(value))
                        },
                        onSmokingChange = { value ->
                            viewModel.onEvent(RiskEvent.SmokingChanged(value))
                        },
                        onSystolicChange = { value ->
                            viewModel.onEvent(RiskEvent.SystolicChanged(value))
                        },
                        onCholesterolChange = { value ->
                            viewModel.onEvent(RiskEvent.CholesterolChanged(value))
                        },
                        onBack = {
                            handleAction(viewModel.onEvent(RiskEvent.GoBack))
                        },
                        onContinue = {
                            handleAction(viewModel.onEvent(RiskEvent.ContinueAssessment))
                        }
                    )
                }
                ResultRoute -> NavEntry(route) {
                    ResultScreen(
                        result = requireNotNull(state.result) {
                            "ResultRoute membutuhkan hasil prediksi."
                        },
                        onBack = { backStack.removeLastOrNull() },
                        onEdit = {
                            handleAction(viewModel.onEvent(RiskEvent.EditData))
                        },
                        onNewAssessment = {
                            handleAction(viewModel.onEvent(RiskEvent.StartNewAssessment))
                        }
                    )
                }
                else -> error("Rute tidak dikenal: $route")
            }
        }
    )
}
