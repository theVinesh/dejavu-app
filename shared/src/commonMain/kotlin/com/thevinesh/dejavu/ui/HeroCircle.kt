package com.thevinesh.dejavu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thevinesh.dejavu.theme.CloudWhite
import com.thevinesh.dejavu.theme.Coral
import com.thevinesh.dejavu.theme.DejaVuElevation
import com.thevinesh.dejavu.theme.DejaVuTheme

@Composable
fun HeroCircle(
    size: Dp,
    modifier: Modifier = Modifier,
    containerColor: Color = Coral,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(DejaVuElevation.Hero, CircleShape, clip = false)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun HeroCirclePreview() {
    DejaVuTheme {
        HeroCircle(size = 200.dp) {
            Text(
                text = "?",
                color = CloudWhite,
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}
