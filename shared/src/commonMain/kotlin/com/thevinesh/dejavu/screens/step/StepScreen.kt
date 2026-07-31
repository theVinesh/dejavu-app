package com.thevinesh.dejavu.screens.step

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thevinesh.dejavu.resources.Res
import com.thevinesh.dejavu.resources.tap
import com.thevinesh.dejavu.theme.CloudWhite
import com.thevinesh.dejavu.theme.DejaVuTheme
import com.thevinesh.dejavu.ui.ActionDock
import com.thevinesh.dejavu.ui.LetterGroup
import com.thevinesh.dejavu.ui.StageScaffold
import com.thevinesh.dejavu.ui.TutorialOverlay
import com.thevinesh.dejavu.ui.fadeInOnEnter
import com.thevinesh.dejavu.ui.shake
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StepScreen(
    phase: StepPhase,
    onNavigateToStep2: () -> Unit,
    onNavigateToWord: () -> Unit,
    viewModel: StepViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navEffect by viewModel.navEffect.collectAsStateWithLifecycle()

    LaunchedEffect(phase) {
        when (phase) {
            StepPhase.Step1 -> viewModel.startStep1()
            StepPhase.Step2 -> viewModel.startStep2()
        }
    }

    LaunchedEffect(navEffect) {
        when (navEffect) {
            StepNavEffect.ToStep2 -> {
                onNavigateToStep2()
                viewModel.consumeNavEffect()
            }
            StepNavEffect.ToWord -> {
                onNavigateToWord()
                viewModel.consumeNavEffect()
            }
            null -> Unit
        }
    }

    StepContent(
        state = state,
        onGroupClick = viewModel::onGroupClick,
        onUndo = viewModel::onUndo,
        onNext = viewModel::onNext,
        onDismissTutorial = viewModel::dismissTutorial
    )
}

@Composable
internal fun StepContent(
    state: StepUiState,
    onGroupClick: (Int) -> Unit,
    onUndo: () -> Unit,
    onNext: () -> Unit,
    onDismissTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    StageScaffold(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "SELECT THE GROUP CONTAINING\nYOUR LETTER",
                color = CloudWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 44.dp, end = 24.dp, bottom = 14.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 4.dp,
                    end = 12.dp,
                    bottom = 124.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(
                    items = state.groups,
                    span = { index, _ ->
                        if (index == state.groups.lastIndex && state.groups.size % 2 == 1) {
                            GridItemSpan(2)
                        } else {
                            GridItemSpan(1)
                        }
                    }
                ) { index, item ->
                    LetterGroup(
                        letters = item.letters,
                        selectionOrders = item.selectionOrders,
                        onClick = { onGroupClick(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fadeInOnEnter()
                    )
                }
            }
        }

        ActionDock(
            onUndo = onUndo,
            onNext = onNext,
            showNext = state.showNext,
            modifier = Modifier.align(Alignment.BottomCenter),
            undoModifier = Modifier.shake(state.shakeTrigger)
        )

        androidx.compose.animation.AnimatedVisibility(
            visible = state.showTapHint,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 88.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.tap),
                contentDescription = null,
                modifier = Modifier.size(150.dp, 160.dp)
            )
        }

        if (state.showTutorial) {
            TutorialOverlay(
                title = state.tutorialTitle,
                detail = state.tutorialDetail,
                onDismiss = onDismissTutorial
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFC0392B)
@Composable
private fun StepContentPreview() {
    DejaVuTheme {
        StepContent(
            state = StepUiState(
                groups = listOf(
                    GroupItem("ABC"),
                    GroupItem("DEF", listOf(1)),
                    GroupItem("GHI"),
                    GroupItem("JKL", listOf(4)),
                    GroupItem("MNO", listOf(2, 3)),
                    GroupItem("PQR"),
                    GroupItem("STU"),
                    GroupItem("VWX"),
                    GroupItem("YZ")
                ),
                selectedCount = 4,
                requiredCount = 4,
                canUndo = true,
                showNext = true
            ),
            onGroupClick = {},
            onUndo = {},
            onNext = {},
            onDismissTutorial = {}
        )
    }
}
