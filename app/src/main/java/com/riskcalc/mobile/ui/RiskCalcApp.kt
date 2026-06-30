package com.riskcalc.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
private fun RiskFormScreen(modifier: Modifier = Modifier) {
    var age by remember { mutableStateOf("") }
    var systolicBp by remember { mutableStateOf("") }
    var totalCholesterol by remember { mutableStateOf("") }
    var isSmoker by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Hasil prediksi akan tampil di sini.") }

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
            value = age,
            onValueChange = { age = it },
            label = { Text("Usia (tahun)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = systolicBp,
            onValueChange = { systolicBp = it },
            label = { Text("Tekanan darah sistolik (mmHg)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = totalCholesterol,
            onValueChange = { totalCholesterol = it },
            label = { Text("Kolesterol total (mg/dL)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
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
                    checked = isSmoker,
                    onCheckedChange = { isSmoker = it }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isSmoker) "Perokok" else "Tidak merokok",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = {
                resultText = "Form siap. Langkah berikutnya: hubungkan ke API FastAPI dan model Perceptron."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Prediksi Risiko")
        }

        ResultCard(resultText = resultText)
    }
}

@Composable
private fun ResultCard(resultText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hasil",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
