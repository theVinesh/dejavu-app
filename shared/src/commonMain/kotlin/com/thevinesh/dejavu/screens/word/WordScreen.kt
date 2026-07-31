package com.thevinesh.dejavu.screens.word

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thevinesh.dejavu.theme.CloudWhite
import com.thevinesh.dejavu.theme.Coral
import com.thevinesh.dejavu.theme.DejaVuTheme
import com.thevinesh.dejavu.theme.StageRed
import com.thevinesh.dejavu.ui.DejaVuButton
import com.thevinesh.dejavu.ui.DejaVuButtonStyle
import com.thevinesh.dejavu.ui.HeroCircle
import com.thevinesh.dejavu.ui.StageScaffold
import com.thevinesh.dejavu.ui.pushOnPress
import com.thevinesh.dejavu.ui.zoomInFrom
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WordScreen(
    viewModel: WordViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RevealContent(
        answer = state.answer,
        onRevealFinished = viewModel::onZoomFinished
    )
}

@Composable
private fun RevealContent(
    answer: String,
    onRevealFinished: () -> Unit = {},
    animateReveal: Boolean = true
) {
    StageScaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp, vertical = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val heroModifier = if (animateReveal) {
                Modifier.zoomInFrom(
                    fromScale = 3f,
                    durationMillis = 2000,
                    onFinished = onRevealFinished
                )
            } else {
                Modifier
            }

            HeroCircle(
                size = 228.dp,
                modifier = heroModifier
            ) {
                Text(
                    text = answer,
                    color = CloudWhite,
                    fontSize = resultFontSize(answer),
                    fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Did I get it right?",
                color = CloudWhite,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(40.dp),
                verticalAlignment = Alignment.Top
            ) {
                FeedbackButton(
                    label = "Yes",
                    thumbUp = true,
                    onClick = {}
                )
                FeedbackButton(
                    label = "No",
                    thumbUp = false,
                    onClick = {}
                )
            }
        }

        RevealActionDock(
            onBack = {},
            onShare = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FeedbackButton(
    label: String,
    thumbUp: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HeroCircle(
            size = 76.dp,
            containerColor = Coral,
            modifier = Modifier
                .pushOnPress(interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        onClick()
                    }
                )
        ) {
            ThumbIcon(thumbUp = thumbUp)
        }

        Text(
            text = label,
            color = CloudWhite,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ThumbIcon(thumbUp: Boolean) {
    Canvas(modifier = Modifier.size(34.dp)) {
        val unit = size.minDimension / 34f
        rotate(if (thumbUp) 0f else 180f) {
            drawRoundRect(
                color = CloudWhite,
                topLeft = Offset(3f * unit, 13f * unit),
                size = Size(6f * unit, 16f * unit),
                cornerRadius = CornerRadius(2f * unit)
            )

            val hand = Path().apply {
                moveTo(11f * unit, 13f * unit)
                lineTo(14f * unit, 13f * unit)
                lineTo(18f * unit, 4.5f * unit)
                cubicTo(
                    18.8f * unit,
                    2.5f * unit,
                    21.8f * unit,
                    3.2f * unit,
                    21.6f * unit,
                    5.4f * unit
                )
                lineTo(21.1f * unit, 11f * unit)
                lineTo(27.2f * unit, 11f * unit)
                cubicTo(
                    30f * unit,
                    11f * unit,
                    31.5f * unit,
                    13.6f * unit,
                    30.5f * unit,
                    16f * unit
                )
                lineTo(27.2f * unit, 25.5f * unit)
                cubicTo(
                    26.6f * unit,
                    27.2f * unit,
                    25.2f * unit,
                    28f * unit,
                    23.4f * unit,
                    28f * unit
                )
                lineTo(11f * unit, 28f * unit)
                close()
            }
            drawPath(path = hand, color = CloudWhite)
        }
    }
}

@Composable
private fun RevealActionDock(
    onBack: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(StageRed.copy(alpha = 0f), StageRed),
                    startY = 0f,
                    endY = 100f
                )
            )
            .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth()) {
                DejaVuButton(
                    text = "back",
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    style = DejaVuButtonStyle.Undo,
                    leadingContent = {
                        Text(
                            text = "↶",
                            color = StageRed,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )

                Spacer(Modifier.width(14.dp))

                DejaVuButton(
                    text = "Share DejaVu",
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    style = DejaVuButtonStyle.Next
                )
            }

            Text(
                text = "Tell a friend about the mind reader",
                color = CloudWhite.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

private fun resultFontSize(answer: String): androidx.compose.ui.unit.TextUnit {
    val adjusted = when {
        answer.length <= 4 -> 50
        answer.length <= 8 -> 38
        else -> 30
    }
    return adjusted.sp
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun RevealContentPreview() {
    DejaVuTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            RevealContent(
                answer = "APPLE",
                animateReveal = false
            )
        }
    }
}
