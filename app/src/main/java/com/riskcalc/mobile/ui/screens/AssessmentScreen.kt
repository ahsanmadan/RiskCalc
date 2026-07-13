package com.riskcalc.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.riskcalc.mobile.R
import com.riskcalc.mobile.ui.components.ResponsiveContent
import com.riskcalc.mobile.ui.components.RiskBackground
import com.riskcalc.mobile.ui.viewmodel.AssessmentStep
import com.riskcalc.mobile.ui.viewmodel.RiskUiState

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
    RiskBackground(modifier = modifier) {
        ResponsiveContent(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(top = 20.dp, bottom = 24.dp)
            ) {
                TextButton(onClick = onBack) {
                    Text(stringResource(R.string.back))
                }
                Text(
                    text = stringResource(R.string.step_progress, state.currentStep.number),
                    modifier = Modifier.padding(top = 18.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                LinearProgressIndicator(
                    progress = { state.currentStep.number / 4f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(8.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                Spacer(modifier = Modifier.height(36.dp))
                QuestionContent(
                    state = state,
                    onAgeChange = onAgeChange,
                    onSmokingChange = onSmokingChange,
                    onSystolicChange = onSystolicChange,
                    onCholesterolChange = onCholesterolChange,
                    onShowHelp = { helpStep = state.currentStep }
                )
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        modifier = Modifier.padding(top = 14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(36.dp))
                HorizontalDivider(modifier = Modifier.padding(top = 36.dp, bottom = 18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.back))
                    }
                    Button(
                        onClick = {
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
                        modifier = Modifier.weight(1.4f)
                    ) {
                        Text(
                            stringResource(
                                if (state.currentStep == AssessmentStep.Cholesterol) {
                                    R.string.see_result
                                } else {
                                    R.string.next
                                }
                            )
                        )
                    }
                }
            }
        }
    }

    if (helpStep != null) {
        ModalBottomSheet(onDismissRequest = { helpStep = null }) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
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
                Button(
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

@Composable
private fun QuestionContent(
    state: RiskUiState,
    onAgeChange: (String) -> Unit,
    onSmokingChange: (Boolean) -> Unit,
    onSystolicChange: (String) -> Unit,
    onCholesterolChange: (String) -> Unit,
    onShowHelp: () -> Unit
) {
    when (state.currentStep) {
        AssessmentStep.Age -> NumericQuestion(
            title = stringResource(R.string.age_question),
            helper = stringResource(R.string.age_helper),
            label = stringResource(R.string.age_label),
            suffix = stringResource(R.string.age_suffix),
            value = state.ageInput,
            onValueChange = onAgeChange,
            keyboardType = KeyboardType.Number,
            isError = state.errorMessage != null
        )
        AssessmentStep.Smoking -> {
            Text(
                text = stringResource(R.string.smoking_question),
                style = MaterialTheme.typography.headlineMedium
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
                    .padding(top = 28.dp)
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
                                )
                            )
                        }
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
    onShowHelp: (() -> Unit)? = null
) {
    Text(text = title, style = MaterialTheme.typography.headlineMedium)
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
            .padding(top = 26.dp),
        label = { Text(label) },
        suffix = { Text(suffix) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        isError = isError,
        textStyle = MaterialTheme.typography.titleLarge
    )
    if (onShowHelp != null) {
        TextButton(onClick = onShowHelp, modifier = Modifier.padding(top = 6.dp)) {
            Text(stringResource(R.string.how_to_know))
        }
    }
}
