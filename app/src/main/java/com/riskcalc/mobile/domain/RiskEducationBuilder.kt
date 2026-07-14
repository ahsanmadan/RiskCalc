package com.riskcalc.mobile.domain

import com.riskcalc.mobile.domain.model.FactorSummary
import com.riskcalc.mobile.domain.model.FactorTone
import com.riskcalc.mobile.domain.model.RiskInput
import com.riskcalc.mobile.domain.model.RiskPrediction
import com.riskcalc.mobile.domain.model.RiskResult

object RiskEducationBuilder {
    fun build(input: RiskInput, prediction: RiskPrediction): RiskResult {
        val factors = listOf(
            FactorSummary(
                title = "Usia",
                value = "${input.age} tahun",
                description = "Usia termasuk faktor yang tidak dapat diubah.",
                tone = FactorTone.Neutral
            ),
            smokingFactor(input.isCurrentSmoker),
            bloodPressureFactor(input.systolicBp),
            cholesterolFactor(input.totalCholesterol)
        )
        return RiskResult(
            input = input,
            prediction = prediction,
            factors = factors,
            tips = buildTips(input, prediction),
            severeBloodPressureWarning = input.systolicBp > 180.0
        )
    }

    private fun smokingFactor(isCurrentSmoker: Boolean) = if (isCurrentSmoker) {
        FactorSummary(
            title = "Status merokok",
            value = "Merokok aktif",
            description = "Merokok termasuk faktor risiko yang dapat diubah.",
            tone = FactorTone.Attention
        )
    } else {
        FactorSummary(
            title = "Status merokok",
            value = "Tidak merokok",
            description = "Pertahankan kebiasaan ini dan hindari asap rokok.",
            tone = FactorTone.Positive
        )
    }

    private fun bloodPressureFactor(systolicBp: Double): FactorSummary {
        val (value, description, tone) = when {
            systolicBp > 180 -> Triple(
                "Sistolik sangat tinggi",
                "Ukur ulang setelah satu menit. Cari bantuan medis bila ada gejala serius.",
                FactorTone.Urgent
            )
            systolicBp >= 140 -> Triple(
                "Sistolik tinggi tahap 2",
                "Perlu perhatian lebih berdasarkan angka sistolik.",
                FactorTone.Attention
            )
            systolicBp >= 130 -> Triple(
                "Sistolik tinggi tahap 1",
                "Perlu dipantau berdasarkan angka sistolik.",
                FactorTone.Attention
            )
            systolicBp >= 120 -> Triple(
                "Sistolik meningkat",
                "Pantau hasil pengukuran dan jaga kebiasaan sehat.",
                FactorTone.Attention
            )
            else -> Triple(
                "Sistolik dalam rentang normal",
                "Masih dalam rentang normal berdasarkan angka sistolik.",
                FactorTone.Positive
            )
        }
        return FactorSummary(
            title = "Tekanan darah",
            value = "$value (${formatNumber(systolicBp)} mmHg)",
            description = description,
            tone = tone
        )
    }

    private fun cholesterolFactor(totalCholesterol: Int): FactorSummary {
        val (value, description, tone) = when {
            totalCholesterol >= 240 -> Triple(
                "Tinggi",
                "Perlu perhatian karena masuk kategori tinggi.",
                FactorTone.Attention
            )
            totalCholesterol >= 200 -> Triple(
                "Batas tinggi",
                "Sudah mendekati kategori tinggi.",
                FactorTone.Attention
            )
            else -> Triple(
                "Optimal",
                "Masih di bawah 200 mg/dL.",
                FactorTone.Positive
            )
        }
        return FactorSummary(
            title = "Kolesterol total",
            value = "$value ($totalCholesterol mg/dL)",
            description = description,
            tone = tone
        )
    }

    private fun buildTips(input: RiskInput, prediction: RiskPrediction): List<String> {
        val tips = mutableListOf<String>()
        if (prediction.classId == 1) {
            tips += "Jadwalkan konsultasi medis untuk evaluasi lebih lanjut."
        } else {
            tips += "Tetap lakukan pemeriksaan kesehatan secara berkala."
        }
        if (input.isCurrentSmoker) {
            tips += "Kurangi dan hentikan kebiasaan merokok dengan bantuan tenaga medis bila perlu."
        }
        if (input.systolicBp >= 120.0) {
            tips += "Ukur tekanan darah secara rutin dan catat hasilnya."
        }
        if (input.totalCholesterol >= 200) {
            tips += "Periksa profil lipid lengkap dan atur pola makan harian."
        }
        tips += "Jaga pola makan, aktif bergerak, tidur cukup, dan kelola stres."
        return tips.distinct().take(5)
    }

    private fun formatNumber(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}
