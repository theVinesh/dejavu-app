package com.thevinesh.dejavu.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thevinesh.dejavu.theme.CloudWhite
import com.thevinesh.dejavu.theme.DejaVuTheme
import com.thevinesh.dejavu.theme.StageRed

@Composable
fun ActionDock(
    onUndo: () -> Unit,
    onNext: () -> Unit,
    showNext: Boolean,
    modifier: Modifier = Modifier,
    undoModifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, StageRed),
                    startY = 0f,
                    endY = 120f
                )
            )
            .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            DejaVuButton(
                text = "undo",
                onClick = onUndo,
                modifier = undoModifier.weight(1f),
                style = DejaVuButtonStyle.Undo,
                leadingContent = {
                    Text(
                        text = "↶",
                        color = StageRed,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )

            AnimatedVisibility(
                visible = showNext,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .weight(if (showNext) 1f else 0.0001f)
                    .padding(start = if (showNext) 14.dp else 0.dp)
            ) {
                DejaVuButton(
                    text = "next",
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    style = DejaVuButtonStyle.Next,
                    trailingContent = {
                        Text(
                            text = "→",
                            color = CloudWhite,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun ActionDockPreview() {
    DejaVuTheme {
        ActionDock(
            onUndo = {},
            onNext = {},
            showNext = true
        )
    }
}
