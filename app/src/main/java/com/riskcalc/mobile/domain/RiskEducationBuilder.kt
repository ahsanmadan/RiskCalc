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
                description = "Usia digunakan model sebagai faktor yang tidak dapat diubah.",
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
            description = "Merokok merupakan faktor risiko yang dapat diubah.",
            tone = FactorTone.Attention
        )
    } else {
        FactorSummary(
            title = "Status merokok",
            value = "Tidak merokok",
            description = "Pertahankan kebiasaan tidak merokok dan hindari asap rokok.",
            tone = FactorTone.Positive
        )
    }

    private fun bloodPressureFactor(systolicBp: Double): FactorSummary {
        val (value, description, tone) = when {
            systolicBp > 180 -> Triple(
                "Sistolik sangat tinggi",
                "Ukur ulang setelah satu menit. Cari bantuan segera bila ada gejala serius.",
                FactorTone.Urgent
            )
            systolicBp >= 140 -> Triple(
                "Sistolik tinggi tahap 2",
                "Kategori edukatif berdasarkan angka sistolik saja.",
                FactorTone.Attention
            )
            systolicBp >= 130 -> Triple(
                "Sistolik tinggi tahap 1",
                "Kategori edukatif berdasarkan angka sistolik saja.",
                FactorTone.Attention
            )
            systolicBp >= 120 -> Triple(
                "Sistolik meningkat",
                "Pantau pengukuran dan pertahankan kebiasaan sehat.",
                FactorTone.Attention
            )
            else -> Triple(
                "Sistolik dalam rentang normal",
                "Kategori edukatif berdasarkan angka sistolik saja.",
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
                "Kolesterol total berada pada kategori tinggi.",
                FactorTone.Attention
            )
            totalCholesterol >= 200 -> Triple(
                "Batas tinggi",
                "Kolesterol total berada pada kategori borderline high.",
                FactorTone.Attention
            )
            else -> Triple(
                "Optimal",
                "Kolesterol total berada di bawah 200 mg/dL.",
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
            tips += "Pertimbangkan berkonsultasi dengan tenaga medis untuk penilaian yang lebih lengkap."
        } else {
            tips += "Hasil lebih rendah bukan berarti tanpa risiko; tetap lakukan pemeriksaan kesehatan berkala."
        }
        if (input.isCurrentSmoker) {
            tips += "Cari dukungan tenaga medis atau layanan berhenti merokok untuk membantu menghentikan kebiasaan merokok."
        }
        if (input.systolicBp >= 120.0) {
            tips += "Ukur tekanan darah secara benar dan diskusikan hasil yang berulang dengan tenaga medis."
        }
        if (input.totalCholesterol >= 200) {
            tips += "Diskusikan pemeriksaan profil lipid lengkap dan pola makan dengan tenaga medis."
        }
        tips += "Pertahankan pola makan seimbang, aktivitas fisik teratur, tidur cukup, dan kelola stres."
        return tips.distinct().take(5)
    }

    private fun formatNumber(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}
