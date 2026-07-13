package com.riskcalc.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class HeartMood { Happy, Calm }

@Composable
fun RiskBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.18f)
                    )
                )
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 36.dp)
                .size(150.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.32f),
                    RoundedCornerShape(bottomStart = 100.dp, topStart = 100.dp)
                )
        )
        content()
    }
}

@Composable
fun ResponsiveContent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            content = content
        )
    }
}

@Composable
fun DisclaimerCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .widthIn(min = 4.dp, max = 4.dp)
                    .height(52.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(100.dp)
                    )
            )
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HeartCompanion(
    mood: HeartMood,
    description: String,
    modifier: Modifier = Modifier
) {
    val heartColor = MaterialTheme.colorScheme.primary
    val faceColor = MaterialTheme.colorScheme.onPrimary
    Canvas(
        modifier = modifier.semantics {
            contentDescription = description
        }
    ) {
        val width = size.width
        val height = size.height
        val heart = Path().apply {
            moveTo(width * 0.50f, height * 0.90f)
            cubicTo(width * 0.40f, height * 0.80f, width * 0.10f, height * 0.61f, width * 0.10f, height * 0.34f)
            cubicTo(width * 0.10f, height * 0.14f, width * 0.27f, height * 0.06f, width * 0.42f, height * 0.14f)
            cubicTo(width * 0.47f, height * 0.17f, width * 0.50f, height * 0.23f, width * 0.50f, height * 0.23f)
            cubicTo(width * 0.50f, height * 0.23f, width * 0.53f, height * 0.17f, width * 0.58f, height * 0.14f)
            cubicTo(width * 0.73f, height * 0.06f, width * 0.90f, height * 0.14f, width * 0.90f, height * 0.34f)
            cubicTo(width * 0.90f, height * 0.61f, width * 0.60f, height * 0.80f, width * 0.50f, height * 0.90f)
            close()
        }
        drawPath(heart, color = heartColor)
        drawCircle(faceColor, radius = width * 0.035f, center = androidx.compose.ui.geometry.Offset(width * 0.40f, height * 0.38f))
        drawCircle(faceColor, radius = width * 0.035f, center = androidx.compose.ui.geometry.Offset(width * 0.60f, height * 0.38f))
        if (mood == HeartMood.Happy) {
            drawArc(
                color = faceColor,
                startAngle = 18f,
                sweepAngle = 144f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(width * 0.37f, height * 0.43f),
                size = androidx.compose.ui.geometry.Size(width * 0.26f, height * 0.18f),
                style = Stroke(width = width * 0.035f, cap = StrokeCap.Round)
            )
        } else {
            drawLine(
                color = faceColor,
                start = androidx.compose.ui.geometry.Offset(width * 0.42f, height * 0.53f),
                end = androidx.compose.ui.geometry.Offset(width * 0.58f, height * 0.53f),
                strokeWidth = width * 0.035f,
                cap = StrokeCap.Round
            )
        }
    }
}
