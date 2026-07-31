package com.thevinesh.dejavu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thevinesh.dejavu.theme.CloudWhite
import com.thevinesh.dejavu.theme.Coral
import com.thevinesh.dejavu.theme.DejaVuElevation
import com.thevinesh.dejavu.theme.DejaVuTheme
import com.thevinesh.dejavu.theme.SelectedCoral
import com.thevinesh.dejavu.theme.SunshineYellow

@Composable
fun LetterGroup(
    letters: String,
    selectionOrders: List<Int>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val isSelected = selectionOrders.isNotEmpty()
    val elevation = if (isSelected) {
        DejaVuElevation.SelectedControl
    } else {
        DejaVuElevation.Control
    }
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier.padding(top = 12.dp, end = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .pushOnPress(
                    interactionSource = interactionSource,
                    enabled = enabled
                )
                .shadow(elevation, CircleShape, clip = false)
                .clip(CircleShape)
                .background(if (isSelected) SelectedCoral else Coral)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = SunshineYellow.copy(alpha = 0.72f),
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                )
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
                .semantics { selected = isSelected },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letters,
                color = CloudWhite,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                textAlign = TextAlign.Center
            )
        }

        if (isSelected) {
            StackedBadges(
                orders = selectionOrders,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-8).dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun LetterGroupPreview() {
    DejaVuTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LetterGroup("ABC", emptyList(), {}, Modifier.fillMaxWidth())
            LetterGroup("DEF", listOf(1), {}, Modifier.fillMaxWidth())
            LetterGroup("MNO", listOf(2, 3), {}, Modifier.fillMaxWidth())
        }
    }
}
