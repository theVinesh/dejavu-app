package com.thevinesh.dejavu.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Modifier.fadeInOnEnter(durationMillis: Int = 1000): Modifier {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = durationMillis),
        label = "fadeIn"
    )
    return this.alpha(alpha)
}

@Composable
fun Modifier.zoomInFrom(
    fromScale: Float = 3f,
    durationMillis: Int = 2000,
    onFinished: () -> Unit = {}
): Modifier {
    val scale = remember { Animatable(fromScale) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(durationMillis = durationMillis))
        onFinished()
    }
    return this.scale(scale.value)
}

@Composable
fun Modifier.entranceThenFadeOut(
    growMillis: Int = 1000,
    holdMillis: Int = 1000,
    fadeMillis: Int = 1000,
    onFinished: () -> Unit = {}
): Modifier {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                scale.animateTo(1f, tween(growMillis, easing = LinearEasing))
            }
            launch {
                alpha.animateTo(1f, tween(growMillis))
            }
        }
        delay(holdMillis.toLong())
        alpha.animateTo(0f, tween(fadeMillis))
        onFinished()
    }
    return this.scale(scale.value).alpha(alpha.value)
}

@Composable
fun Modifier.shake(trigger: Int): Modifier {
    val offsetX = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        haptic.performHapticFeedback(HapticFeedbackType.Reject)
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 300
                0f at 0
                16f at 75
                -16f at 150
                10f at 225
                0f at 300
            }
        )
    }
    return this.offset { IntOffset(offsetX.value.roundToInt(), 0) }
}

