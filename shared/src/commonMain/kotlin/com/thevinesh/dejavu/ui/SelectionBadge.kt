package com.thevinesh.dejavu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thevinesh.dejavu.theme.DejaVuElevation
import com.thevinesh.dejavu.theme.DejaVuTheme
import com.thevinesh.dejavu.theme.PureWhite
import com.thevinesh.dejavu.theme.StageRed
import com.thevinesh.dejavu.theme.SunshineYellow
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelectionBadge(
    order: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(BadgeSize)
            .shadow(DejaVuElevation.Badge, CircleShape, clip = false)
            .clip(CircleShape)
            .background(SunshineYellow)
            .border(2.dp, StageRed.copy(alpha = 0.55f), CircleShape)
            .semantics { contentDescription = "Selection order $order" },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = order.toString(),
            color = PureWhite,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

internal val BadgeSize = 32.dp

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun SelectionBadgePreview() {
    DejaVuTheme {
        SelectionBadge(order = 12)
    }
}
