package com.riskcalc.mobile.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.riskcalc.mobile.ui.components.ResponsiveContent
import com.riskcalc.mobile.ui.theme.RiskCalcTheme
import kotlin.math.absoluteValue

private val WelcomeBackground = Color(0xFF103B39)
private val WelcomePrimaryText = Color(0xFFF7F5EE)
private val WelcomeSecondaryText = Color(0xFFD8E3DD)
private val WelcomeAccent = Color(0xFFF4D7B8)
private val WelcomeAccentText = Color(0xFF163A37)
private val WelcomeCard = Color(0xFF1A4A47)
private val WelcomeOutline = Color(0xFF3E6B67)
private val WelcomeMint = Color(0xFF9DE7D7)

private val IntroHeadlineStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 34.sp,
    lineHeight = 38.sp,
    letterSpacing = (-0.2).sp
)

private val IntroSubtitleStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)

private val IntroButtonStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.2.sp
)

@Composable
fun IntroScreen(
    onStart: () -> Unit,
    modelError: String?,
    modifier: Modifier = Modifier
) {
    var englishEnabled by rememberSaveable { mutableStateOf(false) }
    var animateIn by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(Unit) {
        animateIn = true
    }

    val illustrationAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 140),
        label = "introIllustrationAlpha"
    )
    val illustrationTranslationY by animateFloatAsState(
        targetValue = if (animateIn) 0f else 48f,
        animationSpec = tween(durationMillis = 720, delayMillis = 140),
        label = "introIllustrationTranslationY"
    )

    val title = if (englishEnabled) {
        "Heart Risk Screening"
    } else {
        "Cek Risiko Jantung"
    }

    val subtitle = if (englishEnabled) {
        "A 2-minute self-evaluation of cardiovascular risk factors based on general health indicators."
    } else {
        "Evaluasi mandiri faktor risiko kardiovaskular dalam 2 menit. Berbasis indikator kesehatan umum."
    }

    val disclaimer = if (englishEnabled) {
        "Educational simulation, not a medical diagnosis."
    } else {
        "Simulasi edukatif, bukan diagnosis medis."
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WelcomeBackground)
    ) {
        ResponsiveContent(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                containerColor = WelcomeBackground
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
                    .navigationBarsPadding(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedVisibility(
                        visible = animateIn,
                        enter = fadeIn(
                            animationSpec = tween(durationMillis = 500)
                        ) + slideInVertically(
                            animationSpec = tween(durationMillis = 600)
                        ) { -it / 5 }
                    ) {
                        WelcomeHeader(
                            title = title,
                            subtitle = subtitle,
                            englishEnabled = englishEnabled,
                            onEnglishEnabledChange = { englishEnabled = it }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(vertical = 20.dp)
                            .graphicsLayer {
                                alpha = illustrationAlpha
                                translationY = illustrationTranslationY
                            }
                    ) {
                        WelcomeIllustration(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            pagerState = pagerState
                        )
                    }

                    AnimatedVisibility(
                        visible = animateIn,
                        enter = fadeIn(
                            animationSpec = tween(durationMillis = 520, delayMillis = 220)
                        ) + slideInVertically(
                            animationSpec = tween(durationMillis = 620, delayMillis = 220)
                        ) { it / 4 }
                    ) {
                        WelcomeFooter(
                            disclaimer = disclaimer,
                            modelError = modelError,
                            onGetStartedClick = onStart
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(
    title: String,
    subtitle: String,
    englishEnabled: Boolean,
    onEnglishEnabledChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (englishEnabled) "English" else "Bahasa",
                style = MaterialTheme.typography.labelMedium,
                color = WelcomePrimaryText.copy(alpha = 0.88f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = englishEnabled,
                onCheckedChange = onEnglishEnabledChange,
                thumbContent = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = WelcomeAccent,
                    checkedTrackColor = WelcomeMint.copy(alpha = 0.45f),
                    uncheckedThumbColor = WelcomePrimaryText,
                    uncheckedTrackColor = WelcomeCard
                )
            )
        }

        Text(
            text = title,
            style = IntroHeadlineStyle,
            color = WelcomePrimaryText
        )

        Text(
            text = subtitle,
            style = IntroSubtitleStyle,
            color = WelcomePrimaryText.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WelcomeIllustration(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                beyondViewportPageCount = 1,
                contentPadding = PaddingValues(horizontal = 56.dp),
                pageSpacing = 16.dp
            ) { page ->
                val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue
                val motionFraction = 1f - pageOffset.coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .graphicsLayer {
                            scaleX = lerp(0.85f, 1f, motionFraction)
                            scaleY = lerp(0.85f, 1f, motionFraction)
                            alpha = lerp(0.5f, 1f, motionFraction)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (page) {
                        0 -> HeartBuddy(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                        1 -> IntroIconCard(
                            icon = Icons.Default.MonitorHeart,
                            iconTint = WelcomeMint,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                        else -> IntroIconCard(
                            icon = Icons.Default.LocalHospital,
                            iconTint = WelcomeAccent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }

        OnboardingDots(currentPage = pagerState.currentPage)
    }
}

@Composable
private fun HeartBuddy(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp))
                .background(WelcomeCard)
                .border(
                    width = 2.dp,
                    color = WelcomeOutline,
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(WelcomeAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart mascot",
                        tint = WelcomeAccentText,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(WelcomePrimaryText)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(WelcomePrimaryText)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(WelcomePrimaryText.copy(alpha = 0.92f))
                )
            }
        }

    }
}

@Composable
private fun IntroIconCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp))
                .background(WelcomeCard)
                .border(
                    width = 2.dp,
                    color = WelcomeOutline,
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(WelcomeAccentText.copy(alpha = 0.28f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(54.dp)
                )
            }
        }
    }
}

@Composable
private fun OnboardingDots(currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(if (currentPage == index) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentPage == index) {
                            WelcomeAccent
                        } else {
                            WelcomeOutline.copy(alpha = 0.6f)
                        }
                    )
            )
        }
    }
}

@Composable
private fun WelcomeFooter(
    disclaimer: String,
    modelError: String?,
    onGetStartedClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onGetStartedClick,
            enabled = modelError == null,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WelcomeAccent,
                contentColor = WelcomeAccentText,
                disabledContainerColor = WelcomeAccent.copy(alpha = 0.5f),
                disabledContentColor = WelcomeAccentText.copy(alpha = 0.7f)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Get started",
                    style = IntroButtonStyle
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Continue",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Text(
            text = disclaimer,
            style = MaterialTheme.typography.bodySmall,
            color = WelcomeSecondaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.96f)
        )

        if (modelError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Model belum siap",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = modelError,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Intro Screen")
@Composable
private fun IntroScreenPreview() {
    RiskCalcTheme {
        IntroScreen(
            onStart = {},
            modelError = null
        )
    }
}
