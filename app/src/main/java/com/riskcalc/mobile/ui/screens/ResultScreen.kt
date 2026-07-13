package com.riskcalc.mobile.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riskcalc.mobile.R
import com.riskcalc.mobile.domain.model.FactorSummary
import com.riskcalc.mobile.domain.model.FactorTone
import com.riskcalc.mobile.domain.model.RiskResult
import com.riskcalc.mobile.ui.components.DisclaimerCard
import com.riskcalc.mobile.ui.components.AnimatedEcgGraphic
import com.riskcalc.mobile.ui.components.CareCharacterMood
import com.riskcalc.mobile.ui.components.GeneratedRiskyCharacter
import com.riskcalc.mobile.ui.components.ResponsiveContent
import com.riskcalc.mobile.ui.components.RiskBackground

@Composable
fun ResultScreen(
    result: RiskResult,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onNewAssessment: () -> Unit,
    modifier: Modifier = Modifier
) {
    var heroVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { heroVisible = true }

    RiskBackground(modifier = modifier) {
        ResponsiveContent(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(top = 10.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.back))
                    }
                    Text(
                        text = stringResource(R.string.app_name),
                        modifier = Modifier.padding(top = 14.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = stringResource(R.string.result_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.result_thanks),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AnimatedVisibility(
                    visible = heroVisible,
                    enter = fadeIn(tween(450)) + scaleIn(
                        animationSpec = tween(500),
                        initialScale = 0.94f
                    )
                ) {
                    ResultHero(result = result)
                }
                if (result.severeBloodPressureWarning) {
                    SafetyAlert()
                }
                if (result.prediction.isBorderline) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.borderline_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.borderline_body),
                                modifier = Modifier.padding(top = 6.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                SectionTitle(stringResource(R.string.factor_section))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column {
                        result.factors.forEachIndexed { index, factor ->
                            FactorItem(factor)
                            if (index < result.factors.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 18.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }

                SectionTitle(stringResource(R.string.tips_section))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        result.tips.forEachIndexed { index, tip ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                Text(
                                    text = tip,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                DisclaimerCard(text = stringResource(R.string.disclaimer))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 54.dp),
                        shape = RoundedCornerShape(17.dp)
                    ) {
                        Text(stringResource(R.string.edit_data))
                    }
                    Button(
                        onClick = onNewAssessment,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 54.dp),
                        shape = RoundedCornerShape(17.dp)
                    ) {
                        Text(stringResource(R.string.new_assessment))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 6.dp),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun ResultHero(result: RiskResult) {
    val isHigh = result.prediction.classId == 1
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHigh) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = if (isHigh) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GeneratedRiskyCharacter(
                mood = if (isHigh) {
                    CareCharacterMood.Reassuring
                } else {
                    CareCharacterMood.Cheerful
                },
                description = stringResource(R.string.reassuring_character_description),
                modifier = Modifier.size(164.dp)
            )
            Text(
                text = stringResource(R.string.result_context),
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = result.prediction.displayLabel,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = stringResource(
                    if (isHigh) R.string.high_result_support else R.string.low_result_support
                ),
                modifier = Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            AnimatedEcgGraphic(
                description = stringResource(R.string.ecg_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .heightIn(min = 42.dp)
            )
        }
    }
}

@Composable
private fun SafetyAlert() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = stringResource(R.string.severe_bp_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.severe_bp_body),
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun FactorItem(factor: FactorSummary) {
    val valueColor = when (factor.tone) {
        FactorTone.Neutral -> MaterialTheme.colorScheme.onSurface
        FactorTone.Positive -> MaterialTheme.colorScheme.primary
        FactorTone.Attention -> MaterialTheme.colorScheme.tertiary
        FactorTone.Urgent -> MaterialTheme.colorScheme.error
    }
    Column(modifier = Modifier.padding(18.dp)) {
        Text(
            text = factor.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = factor.value,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.titleLarge,
            color = valueColor
        )
        Text(
            text = factor.description,
            modifier = Modifier.padding(top = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
