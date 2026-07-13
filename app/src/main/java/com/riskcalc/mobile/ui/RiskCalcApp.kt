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
import com.riskcalc.mobile.ui.viewmodel.RiskViewModel

@Composable
fun RiskCalcApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val factory = remember(context) { RiskViewModel.factory(context) }
    val viewModel: RiskViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(IntroRoute)

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
                            viewModel.startNewAssessment()
                            backStack.add(AssessmentRoute)
                        }
                    )
                }
                AssessmentRoute -> NavEntry(route) {
                    AssessmentScreen(
                        state = state,
                        onAgeChange = viewModel::updateAge,
                        onSmokingChange = viewModel::updateSmoking,
                        onSystolicChange = viewModel::updateSystolic,
                        onCholesterolChange = viewModel::updateCholesterol,
                        onBack = {
                            if (!viewModel.previousStep()) {
                                backStack.removeLastOrNull()
                            }
                        },
                        onContinue = {
                            if (viewModel.continueStep()) {
                                backStack.add(ResultRoute)
                            }
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
                            viewModel.editData()
                            backStack.removeLastOrNull()
                        },
                        onNewAssessment = {
                            viewModel.startNewAssessment()
                            backStack.removeLastOrNull()
                        }
                    )
                }
                else -> error("Rute tidak dikenal: $route")
            }
        }
    )
}
