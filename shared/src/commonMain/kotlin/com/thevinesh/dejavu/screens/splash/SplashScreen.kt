package com.thevinesh.dejavu.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thevinesh.dejavu.theme.CloudWhite
import com.thevinesh.dejavu.ui.HeroCircle
import com.thevinesh.dejavu.ui.StageScaffold
import com.thevinesh.dejavu.ui.zoomInFrom

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    StageScaffold {
        HeroCircle(
            size = 240.dp,
            modifier = Modifier
                .align(Alignment.Center)
                .zoomInFrom(
                    fromScale = 3f,
                    durationMillis = 2000,
                    onFinished = onFinished
                )
        ) {
            Text(
                text = "VinCorp",
                color = CloudWhite,
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}
