package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AlarmRed
import com.example.ui.theme.MedsBlue
import com.example.ui.theme.MedsGreenTime
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(3000L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Big image logo using the user-provided illustration
            Image(
                painter = painterResource(id = R.drawable.meds_alarm_illustration),
                contentDescription = "MedsAlarm Logo",
                modifier = Modifier
                    .size(210.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Title: "Meds" (dark blue) and "Alarm" (blood red)
            val titleAnnotated = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MedsBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 42.sp
                    )
                ) {
                    append("Meds")
                }
                withStyle(
                    style = SpanStyle(
                        color = AlarmRed,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 42.sp
                    )
                ) {
                    append("Alarm")
                }
            }

            Text(
                text = titleAnnotated,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 42.sp,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle: "Medication Reminders Made Simple" in dark green
            Text(
                text = "Medication Reminders Made Simple",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = MedsGreenTime
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
        ) {
            RainbowCircleAnimation(sizeDp = 52)
        }
    }
}

@Composable
fun RainbowCircleAnimation(sizeDp: Int = 52) {
    val infiniteTransition = rememberInfiniteTransition(label = "rainbow_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainbow_angle"
    )

    val rainbowColors = listOf(
        Color(0xFFFF1744), // Red
        Color(0xFFFF9100), // Orange
        Color(0xFFFFEA00), // Yellow
        Color(0xFF00E676), // Green
        Color(0xFF00E5FF), // Cyan
        Color(0xFF2979FF), // Blue
        Color(0xFF651FFF), // Indigo
        Color(0xFFD500F9), // Violet
        Color(0xFFFF1744)  // Close circle with Red
    )

    Canvas(
        modifier = Modifier
            .size(sizeDp.dp)
            .rotate(angle)
    ) {
        val strokeWidth = 5.5.dp.toPx()
        val sweepBrush = Brush.sweepGradient(rainbowColors)
        drawCircle(
            brush = sweepBrush,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
