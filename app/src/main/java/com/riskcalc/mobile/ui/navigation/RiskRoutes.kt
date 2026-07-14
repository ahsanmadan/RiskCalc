package com.riskcalc.mobile.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RiskRoute : NavKey

@Serializable
data object IntroRoute : RiskRoute

@Serializable
data object AssessmentRoute : RiskRoute

@Serializable
data object ResultRoute : RiskRoute
