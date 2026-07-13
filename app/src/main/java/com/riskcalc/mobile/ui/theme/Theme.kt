package com.riskcalc.mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = CareCoral,
    onPrimary = OnCareCoral,
    primaryContainer = CareCoralContainer,
    onPrimaryContainer = OnCareCoralContainer,
    secondary = CalmSage,
    onSecondary = OnCalmSage,
    secondaryContainer = CalmSageContainer,
    onSecondaryContainer = OnCalmSageContainer,
    tertiary = WarmGold,
    onTertiary = OnWarmGold,
    tertiaryContainer = WarmGoldContainer,
    onTertiaryContainer = OnWarmGoldContainer,
    background = CareBackground,
    onBackground = CareOnSurface,
    surface = CareSurface,
    onSurface = CareOnSurface,
    surfaceContainer = CareSurfaceContainer,
    outline = CareOutline
)

private val DarkColors = darkColorScheme(
    primary = DarkCareCoral,
    onPrimary = DarkOnCareCoral,
    primaryContainer = DarkCareCoralContainer,
    onPrimaryContainer = DarkOnCareCoralContainer,
    secondary = DarkCalmSage,
    onSecondary = DarkOnCalmSage,
    secondaryContainer = DarkCalmSageContainer,
    onSecondaryContainer = DarkOnCalmSageContainer,
    tertiary = DarkWarmGold,
    onTertiary = DarkOnWarmGold,
    tertiaryContainer = DarkWarmGoldContainer,
    onTertiaryContainer = DarkOnWarmGoldContainer,
    background = DarkCareBackground,
    onBackground = DarkCareOnSurface,
    surface = DarkCareSurface,
    onSurface = DarkCareOnSurface,
    surfaceContainer = DarkCareSurfaceContainer,
    outline = DarkCareOutline
)

@Composable
fun RiskCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> {
            dynamicDarkColorScheme(context)
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
