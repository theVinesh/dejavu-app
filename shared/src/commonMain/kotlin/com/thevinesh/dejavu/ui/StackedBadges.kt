package com.thevinesh.dejavu.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thevinesh.dejavu.theme.DejaVuTheme

private const val BadgesPerRow = 5
private val BadgeStep = 22.dp
private val BadgeRowStep = 24.dp

@Composable
fun StackedBadges(
    orders: List<Int>,
    modifier: Modifier = Modifier
) {
    if (orders.isEmpty()) return

    Layout(
        modifier = modifier,
        content = {
            orders.forEach { order ->
                SelectionBadge(order = order)
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0
                )
            )
        }
        val columns = minOf(orders.size, BadgesPerRow)
        val rows = (orders.size + BadgesPerRow - 1) / BadgesPerRow
        val badgeSize = BadgeSize.roundToPx()
        val stepX = BadgeStep.roundToPx()
        val stepY = BadgeRowStep.roundToPx()
        val width = (badgeSize + (columns - 1) * stepX)
            .coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = (badgeSize + (rows - 1) * stepY)
            .coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(width, height) {
            placeables.forEachIndexed { index, placeable ->
                val row = index / BadgesPerRow
                val column = index % BadgesPerRow
                placeable.placeRelative(
                    x = column * stepX,
                    y = row * stepY
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun StackedBadgesPreview() {
    DejaVuTheme {
        Box(Modifier.padding(16.dp)) {
            StackedBadges(orders = listOf(2, 3))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun WrappedStackedBadgesPreview() {
    DejaVuTheme {
        Box(Modifier.padding(16.dp)) {
            StackedBadges(orders = (1..8).toList())
        }
    }
}
