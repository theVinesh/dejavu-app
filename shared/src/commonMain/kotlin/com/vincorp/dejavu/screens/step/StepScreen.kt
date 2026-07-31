package com.vincorp.dejavu.screens.step

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vincorp.dejavu.resources.Res
import com.vincorp.dejavu.resources.tap
import com.vincorp.dejavu.theme.Background
import com.vincorp.dejavu.theme.Green
import com.vincorp.dejavu.theme.PrimaryText
import com.vincorp.dejavu.theme.Yellow
import com.vincorp.dejavu.theme.rememberCirculaFontFamily
import com.vincorp.dejavu.ui.CircleBackground
import com.vincorp.dejavu.ui.TutorialOverlay
import com.vincorp.dejavu.ui.fadeInOnEnter
import com.vincorp.dejavu.ui.shake
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
    val font = rememberCirculaFontFamily()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(state.groups) { index, item ->
                        CircleBackground(
                            size = 150.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fadeInOnEnter()
                                .clickable { viewModel.onGroupClick(index) }
                        ) {
                            Text(
                                text = item.letters,
                                color = PrimaryText,
                                fontSize = 28.sp,
                                fontFamily = font,
                                textAlign = TextAlign.Center,
                                lineHeight = 30.sp
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = state.showTapHint,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 24.dp, top = 24.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.tap),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp, 160.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Yellow)
                        .shake(state.shakeTrigger)
                        .clickable { viewModel.onUndo() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "undo",
                        color = PrimaryText,
                        fontSize = 30.sp,
                        fontFamily = font,
                        modifier = Modifier.fadeInOnEnter()
                    )
                }

                AnimatedVisibility(
                    visible = state.showNext,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Green)
                            .clickable { viewModel.onNext() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "next",
                            color = PrimaryText,
                            fontSize = 30.sp,
                            fontFamily = font
                        )
                    }
                }
            }
        }

        if (state.showTutorial) {
            TutorialOverlay(
                title = state.tutorialTitle,
                detail = state.tutorialDetail,
                onDismiss = { viewModel.dismissTutorial() }
            )
        }
    }
}
