package com.thevinesh.dejavu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thevinesh.dejavu.theme.DejaVuTheme
import com.thevinesh.dejavu.theme.NightScrim
import com.thevinesh.dejavu.theme.SunshineYellow
import com.thevinesh.dejavu.theme.Teal

@Composable
fun TutorialOverlay(
    title: String,
    detail: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NightScrim),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = title,
                color = SunshineYellow,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = detail,
                color = Teal,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
            DejaVuButton(
                text = "Got it",
                onClick = onDismiss,
                style = DejaVuButtonStyle.Undo,
                size = DejaVuButtonSize.Compact,
                modifier = Modifier
                    .padding(top = 28.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun TutorialOverlayPreview() {
    DejaVuTheme {
        TutorialOverlay(
            title = "Again!, One more time",
            detail = "Tap the groups in which the letters occur",
            onDismiss = {}
        )
    }
}
