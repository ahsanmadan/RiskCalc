package com.riskcalc.mobile.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.riskcalc.mobile.R
import com.riskcalc.mobile.ui.components.ResponsiveContent
import com.riskcalc.mobile.ui.components.RiskBackground
import com.riskcalc.mobile.ui.components.CareCharacterMood
import com.riskcalc.mobile.ui.components.GeneratedRiskyCharacter
import com.riskcalc.mobile.ui.viewmodel.AssessmentStep
import com.riskcalc.mobile.ui.viewmodel.RiskUiState

private val AssessmentStepShell = Color(0xFF154542)
private val AssessmentStepAccentStart = Color(0xFF77D8C6)
private val AssessmentStepAccentEnd = Color(0xFFF4D7B8)
private val AssessmentSoftTeal = Color(0xFFE2F2EE)
private val AssessmentFieldText = Color(0xFF163A37)
private val AssessmentFieldLabel = Color(0xFF214A47)
private val AssessmentFieldOutline = Color(0xFF3E6B67)
private val AssessmentFieldContainer = Color(0xFFF7F5EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    state: RiskUiState,
    onAgeChange: (String) -> Unit,
    onSmokingChange: (Boolean) -> Unit,
    onSystolicChange: (String) -> Unit,
    onCholesterolChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var helpStep by remember { mutableStateOf<AssessmentStep?>(null) }
    var showSevereBpAlert by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = state.currentStep.number / 4f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 260f),
        label = "assessment progress"
    )
    androidx.compose.runtime.LaunchedEffect(Unit) {
        contentVisible = true
    }

    RiskBackground(modifier = modifier) {
        ResponsiveContent(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                bottomBar = {
                    AssessmentActions(
                        onBack = onBack,
                        onContinue = {
                            val systolic = state.systolicInput
                                .replace(',', '.')
                                .toDoubleOrNull()
                            if (
                                state.currentStep == AssessmentStep.Cholesterol &&
                                systolic != null &&
                                systolic > 180.0
                            ) {
                                showSevereBpAlert = true
                            } else {
                                onContinue()
                            }
                        },
                        isLastStep = state.currentStep == AssessmentStep.Cholesterol
                    )
                }
            ) { innerPadding ->
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(
                        animationSpec = tween(450, easing = FastOutSlowInEasing)
                    ) + slideInVertically(
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    ) { it / 10 }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .imePadding()
                            .padding(top = 20.dp, bottom = 28.dp)
                    ) {
                        AssessmentStepperHeader(
                            step = state.currentStep,
                            progress = animatedProgress
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 22.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            AnimatedContent(
                                targetState = state.currentStep,
                                transitionSpec = { motionContentTransform() },
                                label = "assessment question"
                            ) { step ->
                                Column(modifier = Modifier.padding(24.dp)) {
                                    QuestionContent(
                                        step = step,
                                        state = state,
                                        onAgeChange = onAgeChange,
                                        onSmokingChange = onSmokingChange,
                                        onSystolicChange = onSystolicChange,
                                        onCholesterolChange = onCholesterolChange,
                                        onShowHelp = { helpStep = step }
                                    )
                                    AnimatedVisibility(
                                        visible = state.errorMessage != null,
                                        enter = fadeIn(animationSpec = tween(180)) + expandVertically(),
                                        exit = fadeOut(animationSpec = tween(140)) + shrinkVertically()
                                    ) {
                                        if (state.errorMessage != null) {
                                            Text(
                                                text = state.errorMessage,
                                                modifier = Modifier
                                                    .padding(top = 14.dp)
                                                    .semantics { liveRegion = LiveRegionMode.Polite },
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = state.currentStep == AssessmentStep.Smoking,
                            enter = fadeIn(animationSpec = tween(220)) + slideInVertically { it / 4 },
                            exit = fadeOut(animationSpec = tween(160)) + slideOutVertically { it / 6 }
                        ) {
                            if (state.currentStep == AssessmentStep.Smoking) {
                                SupportingMotionHint(
                                    text = "Pilih yang paling sesuai dengan kondisi Anda saat ini."
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = state.currentStep == AssessmentStep.Systolic ||
                                state.currentStep == AssessmentStep.Cholesterol,
                            enter = fadeIn(animationSpec = tween(220)) + slideInVertically { it / 4 },
                            exit = fadeOut(animationSpec = tween(160)) + slideOutVertically { it / 6 }
                        ) {
                            if (
                                state.currentStep == AssessmentStep.Systolic ||
                                state.currentStep == AssessmentStep.Cholesterol
                            ) {
                                SupportingMotionHint(
                                    text = "Gunakan angka hasil pemeriksaan agar hasil simulasi lebih konsisten."
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (helpStep != null) {
        ModalBottomSheet(onDismissRequest = { helpStep = null }) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(220)) + slideInVertically { it / 8 }
                ) {
                    Column {
                        Text(
                            text = stringResource(
                                if (helpStep == AssessmentStep.Systolic) {
                                    R.string.systolic_help_title
                                } else {
                                    R.string.cholesterol_help_title
                                }
                            ),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = stringResource(
                                if (helpStep == AssessmentStep.Systolic) {
                                    R.string.systolic_help_body
                                } else {
                                    R.string.cholesterol_help_body
                                }
                            ),
                            modifier = Modifier.padding(top = 12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = { helpStep = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 24.dp)
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    }
                }
            }
        }
    }

    if (showSevereBpAlert) {
        AlertDialog(
            onDismissRequest = { showSevereBpAlert = false },
            title = { Text(stringResource(R.string.severe_bp_title)) },
            text = { Text(stringResource(R.string.severe_bp_body)) },
            dismissButton = {
                TextButton(onClick = { showSevereBpAlert = false }) {
                    Text(stringResource(R.string.review_data))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSevereBpAlert = false
                        onContinue()
                    }
                ) {
                    Text(stringResource(R.string.understand_continue))
                }
            }
        )
    }
}

private fun androidx.compose.animation.AnimatedContentTransitionScope<AssessmentStep>.motionContentTransform(): ContentTransform {
    val movingForward = targetState.ordinal >= initialState.ordinal
    return (
        slideInHorizontally(
            animationSpec = tween(320, easing = FastOutSlowInEasing)
        ) { fullWidth -> if (movingForward) fullWidth / 5 else -fullWidth / 5 } +
            fadeIn(animationSpec = tween(220)) +
            scaleIn(initialScale = 0.98f, animationSpec = tween(280))
        ) togetherWith (
        slideOutHorizontally(
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) { fullWidth -> if (movingForward) -fullWidth / 7 else fullWidth / 7 } +
            fadeOut(animationSpec = tween(180))
        )
}

@Composable
private fun SupportingMotionHint(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        shape = RoundedCornerShape(18.dp),
        color = AssessmentSoftTeal.copy(alpha = 0.92f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = AssessmentStepShell
        )
    }
}

@Composable
private fun AssessmentStepperHeader(
    step: AssessmentStep,
    progress: Float
) {
    val indicatorColor = lerp(
        start = AssessmentStepAccentStart,
        stop = AssessmentStepAccentEnd,
        fraction = progress.coerceIn(0f, 1f)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = AssessmentStepShell,
        contentColor = Color.White,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Box(
                        modifier = Modifier.size(88.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = step,
                            transitionSpec = { motionContentTransform() },
                            label = "step character"
                        ) { animatedStep ->
                            GeneratedRiskyCharacter(
                                mood = when (animatedStep) {
                                    AssessmentStep.Age -> CareCharacterMood.Cheerful
                                    AssessmentStep.Smoking -> CareCharacterMood.Thinking
                                    AssessmentStep.Systolic -> CareCharacterMood.Reassuring
                                    AssessmentStep.Cholesterol -> CareCharacterMood.Cheerful
                                },
                                description = stringResource(
                                    if (animatedStep == AssessmentStep.Smoking) {
                                        R.string.thinking_character_description
                                    } else {
                                        R.string.happy_character_description
                                    }
                                ),
                                modifier = Modifier.size(76.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = stringResource(R.string.step_progress, step.number),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Text(
                        text = stringResource(stepTitleRes(step)),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.step_support),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.76f)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = indicatorColor,
                trackColor = Color.White.copy(alpha = 0.14f),
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

private fun stepTitleRes(step: AssessmentStep): Int = when (step) {
    AssessmentStep.Age -> R.string.age_label
    AssessmentStep.Smoking -> R.string.smoking_label_short
    AssessmentStep.Systolic -> R.string.systolic_label
    AssessmentStep.Cholesterol -> R.string.cholesterol_label
}

@Composable
private fun AssessmentActions(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    isLastStep: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.98f),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 54.dp),
                shape = RoundedCornerShape(17.dp)
            ) {
                Text(stringResource(R.string.back))
            }
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .weight(1.45f)
                    .heightIn(min = 54.dp),
                shape = RoundedCornerShape(17.dp)
            ) {
                Text(stringResource(if (isLastStep) R.string.see_result else R.string.next))
            }
        }
    }
}

@Composable
private fun QuestionContent(
    step: AssessmentStep,
    state: RiskUiState,
    onAgeChange: (String) -> Unit,
    onSmokingChange: (Boolean) -> Unit,
    onSystolicChange: (String) -> Unit,
    onCholesterolChange: (String) -> Unit,
    onShowHelp: () -> Unit
) {
    when (step) {
        AssessmentStep.Age -> NumericQuestion(
            title = stringResource(R.string.age_question),
            helper = stringResource(R.string.age_helper),
            label = stringResource(R.string.age_label),
            suffix = stringResource(R.string.age_suffix),
            value = state.ageInput,
            onValueChange = onAgeChange,
            keyboardType = KeyboardType.Number,
            isError = state.errorMessage != null,
            focusedTextColor = AssessmentFieldText,
            unfocusedTextColor = AssessmentFieldText,
            focusedLabelColor = AssessmentStepAccentStart,
            unfocusedLabelColor = AssessmentFieldLabel,
            focusedBorderColor = AssessmentStepAccentStart,
            unfocusedBorderColor = AssessmentFieldOutline,
            focusedContainerColor = AssessmentFieldContainer,
            unfocusedContainerColor = AssessmentFieldContainer
        )
        AssessmentStep.Smoking -> {
            Text(
                text = stringResource(R.string.smoking_question),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.smoking_helper),
                modifier = Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                listOf(false, true).forEachIndexed { index, value ->
                    SegmentedButton(
                        selected = state.smokingInput == value,
                        onClick = { onSmokingChange(value) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
                        label = {
                            Text(
                                stringResource(
                                    if (value) R.string.yes_smoking else R.string.no_smoking
                                ),
                                fontWeight = if (state.smokingInput == value) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Medium
                                }
                            )
                        },
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = AssessmentStepShell,
                            activeContentColor = Color.White,
                            inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            activeBorderColor = AssessmentStepShell,
                            inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }
        }
        AssessmentStep.Systolic -> NumericQuestion(
            title = stringResource(R.string.systolic_question),
            helper = stringResource(R.string.systolic_helper),
            label = stringResource(R.string.systolic_label),
            suffix = stringResource(R.string.systolic_suffix),
            value = state.systolicInput,
            onValueChange = onSystolicChange,
            keyboardType = KeyboardType.Decimal,
            isError = state.errorMessage != null,
            onShowHelp = onShowHelp
        )
        AssessmentStep.Cholesterol -> NumericQuestion(
            title = stringResource(R.string.cholesterol_question),
            helper = stringResource(R.string.cholesterol_helper),
            label = stringResource(R.string.cholesterol_label),
            suffix = stringResource(R.string.cholesterol_suffix),
            value = state.cholesterolInput,
            onValueChange = onCholesterolChange,
            keyboardType = KeyboardType.Number,
            isError = state.errorMessage != null,
            onShowHelp = onShowHelp
        )
    }
}

@Composable
private fun NumericQuestion(
    title: String,
    helper: String,
    label: String,
    suffix: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    isError: Boolean,
    focusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor: Color = AssessmentStepShell,
    unfocusedLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedBorderColor: Color = AssessmentStepShell,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    focusedContainerColor: Color = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
    onShowHelp: (() -> Unit)? = null
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = helper,
        modifier = Modifier.padding(top = 10.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        label = { Text(label) },
        suffix = { Text(suffix) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(20.dp),
        textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.24f),
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            cursorColor = AssessmentStepShell,
            focusedLabelColor = focusedLabelColor,
            unfocusedLabelColor = unfocusedLabelColor,
            focusedSuffixColor = focusedTextColor,
            unfocusedSuffixColor = unfocusedTextColor,
            focusedPlaceholderColor = focusedLabelColor,
            unfocusedPlaceholderColor = unfocusedLabelColor
        )
    )
    if (onShowHelp != null) {
        TextButton(onClick = onShowHelp, modifier = Modifier.padding(top = 4.dp)) {
            Text(stringResource(R.string.how_to_know))
        }
    }
}
