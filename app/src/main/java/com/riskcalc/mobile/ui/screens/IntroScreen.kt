package com.riskcalc.mobile.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riskcalc.mobile.R
import com.riskcalc.mobile.ui.components.AnimatedCareCharacter
import com.riskcalc.mobile.ui.components.AnimatedEcgGraphic
import com.riskcalc.mobile.ui.components.CareCharacterMood
import com.riskcalc.mobile.ui.components.DisclaimerCard
import com.riskcalc.mobile.ui.components.ResponsiveContent
import com.riskcalc.mobile.ui.components.RiskBackground

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IntroScreen(
    onStart: () -> Unit,
    modelError: String?,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    RiskBackground(modifier = modifier) {
        ResponsiveContent(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(top = 18.dp, bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(600)) { it / 8 }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(34.dp),
                        color = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                    ) {
                        Column {
                            Column(
                                modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = stringResource(R.string.intro_prompt),
                                    style = MaterialTheme.typography.displaySmall
                                )
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IntroTag(stringResource(R.string.tag_know))
                                    IntroTag(stringResource(R.string.tag_understand))
                                    IntroTag(stringResource(R.string.tag_care))
                                }
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 22.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.care_eyebrow),
                                        modifier = Modifier.fillMaxWidth(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AnimatedCareCharacter(
                                            mood = CareCharacterMood.Cheerful,
                                            description = stringResource(R.string.happy_character_description),
                                            modifier = Modifier.size(190.dp)
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.intro_body),
                                        modifier = Modifier.fillMaxWidth(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    AnimatedEcgGraphic(
                                        description = stringResource(R.string.ecg_description),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp)
                                    )
                                    Button(
                                        onClick = onStart,
                                        enabled = modelError == null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 58.dp),
                                        shape = RoundedCornerShape(18.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.inverseSurface,
                                            contentColor = MaterialTheme.colorScheme.inverseOnSurface
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(R.string.start_assessment),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.assessment_duration),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }

                DisclaimerCard(text = stringResource(R.string.disclaimer))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.intro_data_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                        CareDataRow("01", stringResource(R.string.age_label))
                        CareDataRow("02", stringResource(R.string.smoking_label_short))
                        CareDataRow("03", stringResource(R.string.systolic_label))
                        CareDataRow("04", stringResource(R.string.cholesterol_label))
                    }
                }

                if (modelError != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.model_error_title),
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = modelError, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.offline_private),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun IntroTag(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun CareDataRow(number: String, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text(
                text = number,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
