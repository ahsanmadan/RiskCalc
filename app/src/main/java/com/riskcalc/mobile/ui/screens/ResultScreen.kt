package com.riskcalc.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riskcalc.mobile.R
import com.riskcalc.mobile.domain.model.FactorSummary
import com.riskcalc.mobile.domain.model.FactorTone
import com.riskcalc.mobile.domain.model.RiskResult
import com.riskcalc.mobile.ui.components.DisclaimerCard
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
    RiskBackground(modifier = modifier) {
        ResponsiveContent(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TextButton(onClick = onBack) {
                    Text(stringResource(R.string.back))
                }
                Text(
                    text = stringResource(R.string.result_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                if (result.severeBloodPressureWarning) {
                    SafetyAlert()
                }
                ResultHero(result = result)
                if (result.prediction.isBorderline) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.borderline_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = stringResource(R.string.borderline_body),
                                modifier = Modifier.padding(top = 6.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.factor_section),
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                result.factors.forEach { factor -> FactorCard(factor) }
                Text(
                    text = stringResource(R.string.tips_section),
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        result.tips.forEachIndexed { index, tip ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                Text(
                                    text = tip,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                DisclaimerCard(text = stringResource(R.string.disclaimer))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.edit_data))
                    }
                    Button(
                        onClick = onNewAssessment,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.new_assessment))
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultHero(result: RiskResult) {
    val isHigh = result.prediction.classId == 1
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isHigh) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            },
            contentColor = if (isHigh) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onTertiaryContainer
            }
        )
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = stringResource(R.string.result_context),
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
        }
    }
}

@Composable
private fun SafetyAlert() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
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
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun FactorCard(factor: FactorSummary) {
    val container = when (factor.tone) {
        FactorTone.Neutral -> MaterialTheme.colorScheme.surfaceContainerHigh
        FactorTone.Positive -> MaterialTheme.colorScheme.tertiaryContainer
        FactorTone.Attention -> MaterialTheme.colorScheme.secondaryContainer
        FactorTone.Urgent -> MaterialTheme.colorScheme.errorContainer
    }
    val content = when (factor.tone) {
        FactorTone.Neutral -> MaterialTheme.colorScheme.onSurface
        FactorTone.Positive -> MaterialTheme.colorScheme.onTertiaryContainer
        FactorTone.Attention -> MaterialTheme.colorScheme.onSecondaryContainer
        FactorTone.Urgent -> MaterialTheme.colorScheme.onErrorContainer
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = factor.title, style = MaterialTheme.typography.labelLarge)
            Text(
                text = factor.value,
                modifier = Modifier.padding(top = 3.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = factor.description,
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
