package com.riskcalc.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riskcalc.mobile.ui.viewmodel.RiskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskCalcApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RiskCalc") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        RiskFormScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun RiskFormScreen(
    modifier: Modifier = Modifier,
    viewModel: RiskViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Klasifikasi Risiko Penyakit Jantung Koroner",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Masukkan data dasar pasien untuk mendapatkan prediksi risiko.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = viewModel.age,
            onValueChange = { viewModel.onAgeChange(it) },
            label = { Text("Usia (tahun)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !viewModel.isLoading
        )

        OutlinedTextField(
            value = viewModel.systolicBp,
            onValueChange = { viewModel.onSystolicBpChange(it) },
            label = { Text("Tekanan darah sistolik (mmHg)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !viewModel.isLoading
        )

        OutlinedTextField(
            value = viewModel.totalCholesterol,
            onValueChange = { viewModel.onTotalCholesterolChange(it) },
            label = { Text("Kolesterol total (mg/dL)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !viewModel.isLoading
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Status Merokok",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Switch(
                    checked = viewModel.isSmoker,
                    onCheckedChange = { viewModel.onSmokerChange(it) },
                    enabled = !viewModel.isLoading
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (viewModel.isSmoker) "Perokok" else "Tidak merokok",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = { viewModel.predictRisk() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Memproses...")
                }
            } else {
                Text("Prediksi Risiko")
            }
        }

        ResultCard(
            resultText = viewModel.resultText,
            predictionClass = viewModel.predictionClass,
            explanation = viewModel.explanation,
            recommendations = viewModel.recommendations
        )
    }
}

@Composable
private fun ResultCard(
    resultText: String,
    predictionClass: Int?,
    explanation: String?,
    recommendations: List<String>
) {
    // Tentukan warna berdasarkan kelas prediksi
    val (containerColor, contentColor) = when (predictionClass) {
        1 -> {
            // Risiko Tinggi -> Merah/Pink
            Color(0xFFFFDAD6) to Color(0xFF410002)
        }
        0 -> {
            // Risiko Rendah -> Hijau Muda
            Color(0xFFD1F2D9) to Color(0xFF00391C)
        }
        else -> {
            // Default -> Abu-abu / Neutral secondary container
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hasil Analisis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            if (!explanation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Rekomendasi & Tindakan Pencegahan:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                recommendations.forEach { recommendation ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

