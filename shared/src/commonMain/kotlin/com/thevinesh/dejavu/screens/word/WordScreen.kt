package com.thevinesh.dejavu.screens.word

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thevinesh.dejavu.theme.CloudWhite
import com.thevinesh.dejavu.ui.DejaVuButton
import com.thevinesh.dejavu.ui.DejaVuButtonSize
import com.thevinesh.dejavu.ui.DejaVuButtonStyle
import com.thevinesh.dejavu.ui.HeroCircle
import com.thevinesh.dejavu.ui.StageScaffold
import com.thevinesh.dejavu.ui.zoomInFrom
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun WordScreen(
    viewModel: WordViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    StageScaffold {
        if (!state.physicsEnabled) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                HeroCircle(
                    size = 240.dp,
                    modifier = Modifier
                        .zoomInFrom(
                            fromScale = 3f,
                            durationMillis = 2000,
                            onFinished = { viewModel.onZoomFinished() }
                        )
                        .pointerInput(state.zoomFinished) {
                            if (state.zoomFinished) {
                                detectTapGestures {
                                    haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                    viewModel.onPlayTapped()
                                }
                            }
                        }
                ) {
                    Text(
                        text = state.answer,
                        color = CloudWhite,
                        fontSize = resultFontSize(state.answer),
                        fontFamily = MaterialTheme.typography.displayMedium.fontFamily
                    )
                }
            }

            if (state.showPlayLabel) {
                DejaVuButton(
                    text = "Play with me",
                    onClick = viewModel::onPlayTapped,
                    style = DejaVuButtonStyle.Undo,
                    size = DejaVuButtonSize.Compact,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 24.dp, end = 16.dp)
                )
            }
        } else {
            BouncingWord(
                answer = state.answer,
                onCollision = viewModel::onCollision
            )
        }
    }
}

@Composable
private fun BouncingWord(
    answer: String,
    onCollision: () -> Unit
) {
    val density = LocalDensity.current
    val ballSizePx = with(density) { 120.dp.toPx() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        var position by remember {
            mutableStateOf(
                Offset(
                    (widthPx - ballSizePx) / 2f,
                    (heightPx - ballSizePx) / 2f
                )
            )
        }
        var velocity by remember { mutableStateOf(Offset(520f, 380f)) }

        LaunchedEffect(widthPx, heightPx) {
            var lastTime = 0L
            while (true) {
                withFrameNanos { time ->
                    if (lastTime == 0L) {
                        lastTime = time
                        return@withFrameNanos
                    }
                    val dt = ((time - lastTime) / 1_000_000_000f).coerceIn(0f, 0.032f)
                    lastTime = time

                    var next = position + velocity * dt
                    var vx = velocity.x
                    var vy = velocity.y
                    var collided = false

                    if (next.x <= 0f) {
                        next = next.copy(x = 0f)
                        vx = -vx
                        collided = true
                    } else if (next.x + ballSizePx >= widthPx) {
                        next = next.copy(x = widthPx - ballSizePx)
                        vx = -vx
                        collided = true
                    }

                    if (next.y <= 0f) {
                        next = next.copy(y = 0f)
                        vy = -vy
                        collided = true
                    } else if (next.y + ballSizePx >= heightPx) {
                        next = next.copy(y = heightPx - ballSizePx)
                        vy = -vy
                        collided = true
                    }

                    position = next
                    velocity = Offset(vx, vy)
                    if (collided) onCollision()
                }
            }
        }

        HeroCircle(
            size = 120.dp,
            modifier = Modifier
                .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            position = Offset(
                                (position.x + dragAmount.x).coerceIn(0f, widthPx - ballSizePx),
                                (position.y + dragAmount.y).coerceIn(0f, heightPx - ballSizePx)
                            )
                            velocity = Offset(dragAmount.x * 40f, dragAmount.y * 40f)
                        }
                    )
                }
        ) {
            Text(
                text = answer,
                color = CloudWhite,
                fontSize = resultFontSize(answer, compact = true),
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily
            )
        }
    }
}

private fun resultFontSize(answer: String, compact: Boolean = false): androidx.compose.ui.unit.TextUnit {
    val base = if (compact) 28 else 50
    val adjusted = when {
        answer.length <= 4 -> base
        answer.length <= 8 -> base - 12
        else -> base - 20
    }
    return adjusted.sp
}
