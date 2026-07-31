package com.vincorp.dejavu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vincorp.dejavu.theme.Green
import com.vincorp.dejavu.theme.Yellow
import com.vincorp.dejavu.theme.rememberCirculaFontFamily

@Composable
fun TutorialOverlay(
    title: String,
    detail: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val font = rememberCirculaFontFamily()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = title,
                color = Yellow,
                fontSize = 30.sp,
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = detail,
                color = Green,
                fontSize = 20.sp,
                fontFamily = font,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Got it",
                color = Yellow,
                fontSize = 18.sp,
                fontFamily = font,
                modifier = Modifier
                    .padding(top = 28.dp)
                    .clickable(role = Role.Button, onClick = onDismiss)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )
        }
    }
}
