package com.riskcalc.mobile.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class HeartMood { Happy, Calm }
enum class CareCharacterMood { Cheerful, Thinking, Reassuring }

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

@Composable
fun AnimatedCareCharacter(
    mood: CareCharacterMood,
    description: String,
    modifier: Modifier = Modifier
) {
    val animation = rememberInfiniteTransition(label = "care character")
    val floatOffset by animation.animateFloat(
        initialValue = 3f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "character float"
    )
    val breathScale by animation.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "character breath"
    )
    val blink by animation.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3400
                1f at 0
                1f at 2400
                0.08f at 2480
                1f at 2570
                1f at 3400
            }
        ),
        label = "character blink"
    )
    val density = LocalDensity.current
    val bodyColor = when (mood) {
        CareCharacterMood.Cheerful -> MaterialTheme.colorScheme.primary
        CareCharacterMood.Thinking -> MaterialTheme.colorScheme.secondary
        CareCharacterMood.Reassuring -> MaterialTheme.colorScheme.primary
    }
    val faceColor = when (mood) {
        CareCharacterMood.Thinking -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onPrimary
    }
    val cheekColor = MaterialTheme.colorScheme.tertiaryContainer

    Canvas(
        modifier = modifier
            .graphicsLayer {
                translationY = with(density) { floatOffset.dp.toPx() }
                scaleX = breathScale
                scaleY = breathScale
            }
            .semantics { contentDescription = description }
    ) {
        val width = size.width
        val height = size.height
        val body = Path().apply {
            moveTo(width * 0.16f, height * 0.80f)
            lineTo(width * 0.16f, height * 0.43f)
            cubicTo(width * 0.16f, height * 0.17f, width * 0.31f, height * 0.07f, width * 0.50f, height * 0.07f)
            cubicTo(width * 0.69f, height * 0.07f, width * 0.84f, height * 0.17f, width * 0.84f, height * 0.43f)
            lineTo(width * 0.84f, height * 0.80f)
            cubicTo(width * 0.75f, height * 0.91f, width * 0.25f, height * 0.91f, width * 0.16f, height * 0.80f)
            close()
        }
        drawPath(body, bodyColor)

        drawLine(
            color = bodyColor,
            start = Offset(width * 0.17f, height * 0.58f),
            end = Offset(width * 0.06f, height * 0.68f),
            strokeWidth = width * 0.045f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = bodyColor,
            start = Offset(width * 0.83f, height * 0.58f),
            end = Offset(width * 0.94f, height * 0.50f),
            strokeWidth = width * 0.045f,
            cap = StrokeCap.Round
        )

        val eyeHeight = height * 0.055f * blink
        drawOval(
            color = faceColor,
            topLeft = Offset(width * 0.35f, height * 0.40f - eyeHeight / 2),
            size = Size(width * 0.055f, eyeHeight.coerceAtLeast(2f))
        )
        drawOval(
            color = faceColor,
            topLeft = Offset(width * 0.59f, height * 0.40f - eyeHeight / 2),
            size = Size(width * 0.055f, eyeHeight.coerceAtLeast(2f))
        )
        drawCircle(cheekColor, width * 0.035f, Offset(width * 0.29f, height * 0.51f))
        drawCircle(cheekColor, width * 0.035f, Offset(width * 0.71f, height * 0.51f))

        when (mood) {
            CareCharacterMood.Cheerful -> drawArc(
                color = faceColor,
                startAngle = 16f,
                sweepAngle = 148f,
                useCenter = false,
                topLeft = Offset(width * 0.39f, height * 0.46f),
                size = Size(width * 0.22f, height * 0.16f),
                style = Stroke(width * 0.035f, cap = StrokeCap.Round)
            )
            CareCharacterMood.Thinking -> {
                drawCircle(faceColor, width * 0.032f, Offset(width * 0.50f, height * 0.55f), style = Stroke(width * 0.02f))
                drawCircle(faceColor, width * 0.022f, Offset(width * 0.76f, height * 0.24f))
                drawCircle(faceColor, width * 0.012f, Offset(width * 0.70f, height * 0.31f))
            }
            CareCharacterMood.Reassuring -> drawArc(
                color = faceColor,
                startAngle = 25f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(width * 0.41f, height * 0.49f),
                size = Size(width * 0.18f, height * 0.11f),
                style = Stroke(width * 0.026f, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun AnimatedEcgGraphic(
    description: String,
    modifier: Modifier = Modifier
) {
    val animation = rememberInfiniteTransition(label = "ECG graphic")
    val progress by animation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2200)),
        label = "ECG pulse"
    )
    val lineColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)

    Canvas(modifier = modifier.semantics { contentDescription = description }) {
        fun normalizedY(x: Float): Float = when {
            x < 0.28f -> 0.56f
            x < 0.36f -> 0.56f + ((x - 0.28f) / 0.08f) * 0.14f
            x < 0.44f -> 0.70f - ((x - 0.36f) / 0.08f) * 0.47f
            x < 0.52f -> 0.23f + ((x - 0.44f) / 0.08f) * 0.47f
            x < 0.60f -> 0.70f - ((x - 0.52f) / 0.08f) * 0.14f
            else -> 0.56f
        }
        drawLine(trackColor, Offset(0f, size.height * 0.56f), Offset(size.width, size.height * 0.56f), strokeWidth = 3f)
        val path = Path()
        repeat(101) { index ->
            val x = index / 100f
            val point = Offset(size.width * x, size.height * normalizedY(x))
            if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        drawPath(path, lineColor, style = Stroke(width = size.height * 0.07f, cap = StrokeCap.Round))
        drawCircle(
            color = lineColor,
            radius = size.height * 0.10f,
            center = Offset(size.width * progress, size.height * normalizedY(progress))
        )
    }
}
