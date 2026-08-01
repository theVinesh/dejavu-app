package com.thevinesh.dejavu.screens.word

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
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
import com.thevinesh.dejavu.theme.SelectedCoral
import com.thevinesh.dejavu.theme.StageRed
import com.thevinesh.dejavu.theme.SunshineYellow
import com.thevinesh.dejavu.ui.DejaVuButton
import com.thevinesh.dejavu.ui.DejaVuButtonStyle
import com.thevinesh.dejavu.ui.HeroCircle
import com.thevinesh.dejavu.ui.StageScaffold
import com.thevinesh.dejavu.ui.fadeInOnEnter
import com.thevinesh.dejavu.ui.pushOnPress
import com.thevinesh.dejavu.ui.zoomInFrom
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private enum class CuePhase {
    Idle,
    Arrow,
    Caption
}

@Composable
fun WordScreen(
    onRestart: () -> Unit,
    viewModel: WordViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RevealContent(
        answer = state.answer,
        feedback = state.feedback,
        feedbackAnimationId = state.feedbackAnimationId,
        onRevealFinished = viewModel::onZoomFinished,
        onFeedback = viewModel::onFeedback,
        onBack = onRestart,
        onShare = viewModel::onShare
    )
}

@Composable
private fun RevealContent(
    answer: String,
    feedback: RevealFeedback,
    feedbackAnimationId: Int,
    onRevealFinished: () -> Unit = {},
    onFeedback: (RevealFeedback) -> Unit = {},
    onBack: () -> Unit = {},
    onShare: () -> Unit = {},
    animateReveal: Boolean = true
) {
    var cuePhase by remember { mutableStateOf(CuePhase.Idle) }
    val emojiEmissions = remember { mutableStateListOf<Int>() }

    LaunchedEffect(feedback) {
        if (feedback == RevealFeedback.None) {
            cuePhase = CuePhase.Idle
            return@LaunchedEffect
        }
        delay(900)
        cuePhase = CuePhase.Arrow
        delay(420)
        cuePhase = CuePhase.Caption
    }

    LaunchedEffect(feedbackAnimationId) {
        if (feedbackAnimationId > 0) {
            emojiEmissions += feedbackAnimationId
        }
    }

    val caption = when (feedback) {
        RevealFeedback.Positive -> "Tell a friend about the mind reader"
        RevealFeedback.Negative -> "Give me another chance?"
        RevealFeedback.None -> ""
    }

    StageScaffold {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
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

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Did I get it right?",
                    color = CloudWhite,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(40.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        FeedbackButton(
                            label = "Yes",
                            thumbUp = true,
                            selected = feedback == RevealFeedback.Positive,
                            enabled = feedback == RevealFeedback.None ||
                                feedback == RevealFeedback.Positive,
                            onClick = { onFeedback(RevealFeedback.Positive) }
                        )
                        FeedbackButton(
                            label = "No",
                            thumbUp = false,
                            selected = feedback == RevealFeedback.Negative,
                            enabled = feedback == RevealFeedback.None ||
                                feedback == RevealFeedback.Negative,
                            onClick = { onFeedback(RevealFeedback.Negative) }
                        )
                    }

                    emojiEmissions.forEach { emissionId ->
                        key(emissionId) {
                            FloatingEmoji(
                                emoji = if (feedback == RevealFeedback.Positive) "😄" else "😢",
                                alignStart = feedback == RevealFeedback.Positive,
                                onFinished = { emojiEmissions.remove(emissionId) },
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            if (cuePhase == CuePhase.Arrow || cuePhase == CuePhase.Caption) {
                BentCueArrow(
                    pointToShare = feedback == RevealFeedback.Positive,
                    modifier = Modifier
                        .fillMaxSize()
                        .fadeInOnEnter(durationMillis = 500)
                )
            }

            RevealActionDock(
                caption = caption,
                showCaption = cuePhase == CuePhase.Caption,
                feedback = feedback,
                onBack = onBack,
                onShare = onShare,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun FloatingEmoji(
    emoji: String,
    alignStart: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(emoji) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
        onFinished()
    }
    val density = LocalDensity.current
    val risePx = with(density) { 72.dp.toPx() }
    val sidePx = with(density) { 58.dp.toPx() }

    Text(
        text = emoji,
        fontSize = 34.sp,
        modifier = modifier
            .offset {
                val x = if (alignStart) -sidePx else sidePx
                androidx.compose.ui.unit.IntOffset(
                    x = x.toInt(),
                    y = (-risePx * progress.value).toInt()
                )
            }
            .alpha(1f - progress.value)
    )
}

@Composable
private fun BentCueArrow(
    pointToShare: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val endY = size.height - 116.dp.toPx()
        val end = Offset(
            x = if (pointToShare) size.width * 0.75f else size.width * 0.25f,
            y = endY
        )
        val start = Offset(
            x = size.width * 0.5f,
            y = minOf(452.dp.toPx(), endY - 130.dp.toPx())
        )
        val control1 = Offset(
            x = start.x,
            y = start.y + 48.dp.toPx()
        )
        val control2 = Offset(
            x = end.x + if (pointToShare) -22.dp.toPx() else 22.dp.toPx(),
            y = end.y - 42.dp.toPx()
        )

        val curve = Path().apply {
            moveTo(start.x, start.y)
            cubicTo(
                control1.x,
                control1.y,
                control2.x,
                control2.y,
                end.x,
                end.y
            )
        }
        val stroke = SunshineYellow.copy(alpha = 0.88f)
        drawPath(
            path = curve,
            color = stroke,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        val tangentX = end.x - control2.x
        val tangentY = end.y - control2.y
        val angle = atan2(tangentY, tangentX)
        val head = 14.dp.toPx()
        val left = Offset(
            x = end.x - head * cos(angle - 0.55f),
            y = end.y - head * sin(angle - 0.55f)
        )
        val right = Offset(
            x = end.x - head * cos(angle + 0.55f),
            y = end.y - head * sin(angle + 0.55f)
        )
        val headPath = Path().apply {
            moveTo(end.x, end.y)
            lineTo(left.x, left.y)
            moveTo(end.x, end.y)
            lineTo(right.x, right.y)
        }
        drawPath(
            path = headPath,
            color = stroke,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun FeedbackButton(
    label: String,
    thumbUp: Boolean,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HeroCircle(
            size = 76.dp,
            containerColor = if (selected) SelectedCoral else Coral,
            modifier = Modifier
                .pushOnPress(interactionSource, enabled = enabled)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        onClick()
                    }
                )
                .alpha(if (enabled || selected) 1f else 0.55f)
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
    caption: String,
    showCaption: Boolean,
    feedback: RevealFeedback,
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
                    modifier = Modifier
                        .weight(1f)
                        .attentionWiggle(
                            enabled = showCaption && feedback == RevealFeedback.Negative
                        ),
                    style = DejaVuButtonStyle.Undo,
                    leadingContent = {
                        Text(
                            text = "↶",
                            color = StageRed,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )

                Spacer(modifier = Modifier.width(14.dp))

                DejaVuButton(
                    text = "Share DejaVu",
                    onClick = onShare,
                    modifier = Modifier
                        .weight(1f)
                        .attentionWiggle(
                            enabled = showCaption && feedback == RevealFeedback.Positive
                        ),
                    style = DejaVuButtonStyle.Next
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .padding(top = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (showCaption && caption.isNotEmpty()) {
                    Text(
                        text = caption,
                        color = CloudWhite.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fadeInOnEnter(durationMillis = 450)
                    )
                }
            }
        }
    }
}

@Composable
private fun Modifier.attentionWiggle(enabled: Boolean): Modifier {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(enabled) {
        if (!enabled) {
            rotation.snapTo(0f)
            return@LaunchedEffect
        }

        while (true) {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 340
                    0f at 0
                    -2.5f at 55
                    2.5f at 110
                    -1.8f at 165
                    1.8f at 220
                    0f at 300
                }
            )
            delay(5_000)
        }
    }

    return graphicsLayer { rotationZ = rotation.value }
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
                feedback = RevealFeedback.None,
                feedbackAnimationId = 0,
                animateReveal = false
            )
        }
    }
}
