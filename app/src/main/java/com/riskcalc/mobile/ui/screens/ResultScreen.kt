package com.riskcalc.mobile.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riskcalc.mobile.R
import com.riskcalc.mobile.domain.model.FactorSummary
import com.riskcalc.mobile.domain.model.FactorTone
import com.riskcalc.mobile.domain.model.RiskResult
import com.riskcalc.mobile.ui.components.AnimatedEcgGraphic
import com.riskcalc.mobile.ui.components.CareCharacterMood
import com.riskcalc.mobile.ui.components.GeneratedRiskyCharacter
import com.riskcalc.mobile.ui.components.ResponsiveContent
import com.riskcalc.mobile.ui.components.RiskBackground

private val ResultLowHero = Color(0xFFDFF6EE)
private val ResultLowHeroText = Color(0xFF17463E)
private val ResultHighHero = Color(0xFFF9E2D7)
private val ResultHighHeroText = Color(0xFF6B3422)
private val ResultBadgeLow = Color(0xFF74D6BF)
private val ResultBadgeHigh = Color(0xFFF3A68A)
private val ResultSectionSurface = Color(0xFFF7FBF9)
private val ResultTipSurface = Color(0xFFEEF7F4)
private val ResultDisclaimerSurface = Color(0xFF214A47)
private val ResultDisclaimerAccent = Color(0xFFF4D7B8)

@Composable
fun ResultScreen(
    result: RiskResult,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onNewAssessment: () -> Unit,
    modifier: Modifier = Modifier
) {
    var heroVisible by remember { mutableStateOf(false) }
    var sectionsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        heroVisible = true
        kotlinx.coroutines.delay(140)
        sectionsVisible = true
    }

    RiskBackground(modifier = modifier) {
        ResponsiveContent(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(top = 12.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ResultTopBar(onBack = onBack)

                Text(
                    text = stringResource(R.string.result_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
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

                AnimatedVisibility(
                    visible = sectionsVisible && result.severeBloodPressureWarning,
                    enter = fadeIn(tween(220)) + slideInVertically(
                        initialOffsetY = { it / 5 },
                        animationSpec = tween(320)
                    ) + expandVertically(animationSpec = tween(280))
                ) {
                    if (result.severeBloodPressureWarning) {
                        SafetyAlert()
                    }
                }

                AnimatedVisibility(
                    visible = sectionsVisible && result.prediction.isBorderline,
                    enter = fadeIn(tween(220)) + slideInVertically(
                        initialOffsetY = { it / 5 },
                        animationSpec = tween(340)
                    ) + expandVertically(animationSpec = tween(300))
                ) {
                    if (result.prediction.isBorderline) {
                        BorderlineCard()
                    }
                }

                AnimatedVisibility(
                    visible = sectionsVisible,
                    enter = fadeIn(tween(240)) + slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = tween(360)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        SectionTitle(stringResource(R.string.factor_section))
                        result.factors.forEach { factor ->
                            FactorItem(factor = factor)
                        }
                    }
                }

                AnimatedVisibility(
                    visible = sectionsVisible,
                    enter = fadeIn(tween(280)) + slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = tween(420)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        SectionTitle(stringResource(R.string.tips_section))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ResultSectionSurface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                result.tips.forEachIndexed { index, tip ->
                                    TipItem(
                                        index = index + 1,
                                        tip = tip
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = sectionsVisible,
                    enter = fadeIn(tween(320)) + slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = tween(460)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        MedicalDisclaimerCard(text = stringResource(R.string.disclaimer))
                        ResultFooterActions(
                            onEdit = onEdit,
                            onNewAssessment = onNewAssessment
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.back))
        }
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun ResultHero(result: RiskResult) {
    val isHigh = result.prediction.classId == 1
    val heroColor = if (isHigh) ResultHighHero else ResultLowHero
    val heroTextColor = if (isHigh) ResultHighHeroText else ResultLowHeroText
    val heroBadgeColor = if (isHigh) ResultBadgeHigh else ResultBadgeLow

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
        colors = CardDefaults.cardColors(
            containerColor = heroColor,
            contentColor = heroTextColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GeneratedRiskyCharacter(
                    mood = if (isHigh) {
                        CareCharacterMood.Reassuring
                    } else {
                        CareCharacterMood.Cheerful
                    },
                    description = stringResource(R.string.reassuring_character_description),
                    modifier = Modifier.size(124.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = heroBadgeColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isHigh) Icons.Default.LocalFireDepartment else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = heroTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = stringResource(R.string.result_context),
                                style = MaterialTheme.typography.labelLarge,
                                color = heroTextColor
                            )
                        }
                    }
                    Text(
                        text = result.prediction.displayLabel,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = heroTextColor
                    )
                    Text(
                        text = stringResource(
                            if (isHigh) R.string.high_result_support else R.string.low_result_support
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = heroTextColor.copy(alpha = 0.92f)
                    )
                }
            }

            AnimatedEcgGraphic(
                description = stringResource(R.string.ecg_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            )
        }
    }
}

@Composable
private fun BorderlineCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.borderline_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.borderline_body),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SafetyAlert() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(22.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.severe_bp_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.severe_bp_body),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FactorItem(factor: FactorSummary) {
    val icon = factorIcon(factor)
    val accentColor = factorAccent(factor.tone)
    val accentTextColor = factorAccentContent(factor.tone)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ResultSectionSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(18.dp),
                color = accentColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentTextColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = factor.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = factor.value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentTextColor
                )
                Text(
                    text = factor.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TipItem(
    index: Int,
    tip: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ResultTipSurface)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$index",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Saran $index",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MedicalDisclaimerCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ResultDisclaimerSurface,
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(62.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(ResultDisclaimerAccent)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Catatan medis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ResultDisclaimerAccent
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.92f)
                )
            }
        }
    }
}

@Composable
private fun ResultFooterActions(
    onEdit: () -> Unit,
    onNewAssessment: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(stringResource(R.string.edit_data))
            }
        }
        Button(
            onClick = onNewAssessment,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(stringResource(R.string.new_assessment))
            }
        }
    }
}

private fun factorIcon(factor: FactorSummary): ImageVector {
    val title = factor.title.lowercase()
    return when {
        "usia" in title -> Icons.Default.Cake
        "merokok" in title -> Icons.Default.SmokingRooms
        "sistolik" in title || "tekanan" in title -> Icons.Default.MonitorHeart
        "kolesterol" in title -> Icons.Default.WaterDrop
        factor.tone == FactorTone.Urgent -> Icons.Default.WarningAmber
        else -> Icons.Default.Favorite
    }
}

private fun factorAccent(tone: FactorTone): Color = when (tone) {
    FactorTone.Neutral -> Color(0xFFE8F0ED)
    FactorTone.Positive -> Color(0xFFDDF4EC)
    FactorTone.Attention -> Color(0xFFFDF0D8)
    FactorTone.Urgent -> Color(0xFFF9E0D7)
}

private fun factorAccentContent(tone: FactorTone): Color = when (tone) {
    FactorTone.Neutral -> Color(0xFF35514B)
    FactorTone.Positive -> Color(0xFF195948)
    FactorTone.Attention -> Color(0xFF7B5B17)
    FactorTone.Urgent -> Color(0xFF90462E)
}
