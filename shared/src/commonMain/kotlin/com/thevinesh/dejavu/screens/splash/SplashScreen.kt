package com.thevinesh.dejavu.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thevinesh.dejavu.theme.Background
import com.thevinesh.dejavu.theme.PrimaryText
import com.thevinesh.dejavu.theme.rememberCirculaFontFamily
import com.thevinesh.dejavu.ui.CircleBackground
import com.thevinesh.dejavu.ui.zoomInFrom
import androidx.compose.foundation.background

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val font = rememberCirculaFontFamily()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        CircleBackground(
            size = 240.dp,
            modifier = Modifier.zoomInFrom(fromScale = 3f, durationMillis = 2000, onFinished = onFinished)
        ) {
            Text(
                text = "VinCorp",
                color = PrimaryText,
                fontSize = 50.sp,
                fontFamily = font
            )
        }
    }
}
