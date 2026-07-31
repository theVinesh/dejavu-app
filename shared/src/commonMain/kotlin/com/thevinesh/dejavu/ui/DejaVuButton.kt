package com.thevinesh.dejavu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thevinesh.dejavu.theme.CloudWhite
import com.thevinesh.dejavu.theme.Coral
import com.thevinesh.dejavu.theme.DejaVuElevation
import com.thevinesh.dejavu.theme.DejaVuTheme
import com.thevinesh.dejavu.theme.StageRed
import com.thevinesh.dejavu.theme.SunshineYellow
import com.thevinesh.dejavu.theme.Teal

enum class DejaVuButtonStyle {
    Coral,
    Undo,
    Next
}

enum class DejaVuButtonSize {
    Standard,
    Compact
}

@Composable
fun DejaVuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DejaVuButtonStyle = DejaVuButtonStyle.Coral,
    size: DejaVuButtonSize = DejaVuButtonSize.Standard,
    enabled: Boolean = true,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val containerColor: Color
    val contentColor: Color
    when (style) {
        DejaVuButtonStyle.Coral -> {
            containerColor = Coral
            contentColor = CloudWhite
        }
        DejaVuButtonStyle.Undo -> {
            containerColor = SunshineYellow
            contentColor = StageRed
        }
        DejaVuButtonStyle.Next -> {
            containerColor = Teal
            contentColor = CloudWhite
        }
    }
    val minHeight = if (size == DejaVuButtonSize.Compact) 44.dp else 64.dp
    val horizontalPadding = if (size == DejaVuButtonSize.Compact) 16.dp else 24.dp
    val verticalPadding = if (size == DejaVuButtonSize.Compact) 8.dp else 14.dp
    val textStyle = if (size == DejaVuButtonSize.Compact) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelLarge
    }

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .shadow(DejaVuElevation.Control, CircleShape, clip = false)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .alpha(if (enabled) 1f else 0.55f)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent?.invoke()
        Text(
            text = text,
            color = contentColor,
            style = textStyle,
            modifier = Modifier.padding(
                start = if (leadingContent == null) 0.dp else 10.dp,
                end = if (trailingContent == null) 0.dp else 10.dp
            )
        )
        trailingContent?.invoke()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun DejaVuButtonPreview() {
    DejaVuTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DejaVuButton("next", {}, Modifier.fillMaxWidth())
            DejaVuButton(
                text = "undo",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                style = DejaVuButtonStyle.Undo,
                leadingContent = { Text("↶", color = StageRed) }
            )
            DejaVuButton(
                text = "next",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                style = DejaVuButtonStyle.Next,
                trailingContent = { Text("→", color = CloudWhite) }
            )
        }
    }
}
