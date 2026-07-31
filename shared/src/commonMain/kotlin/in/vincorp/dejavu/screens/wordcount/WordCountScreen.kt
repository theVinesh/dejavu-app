package in.vincorp.dejavu.screens.wordcount

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import in.vincorp.dejavu.theme.Background
import in.vincorp.dejavu.theme.LighterTheme
import in.vincorp.dejavu.theme.PrimaryText
import in.vincorp.dejavu.theme.rememberCirculaFontFamily
import in.vincorp.dejavu.ui.CircleBackground
import in.vincorp.dejavu.ui.TutorialOverlay
import in.vincorp.dejavu.ui.entranceThenFadeOut
import in.vincorp.dejavu.ui.fadeInOnEnter
import in.vincorp.dejavu.ui.shake
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WordCountScreen(
    onNavigateToStep1: () -> Unit,
    onNavigateToWord: () -> Unit,
    viewModel: WordCountViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navEffect by viewModel.navEffect.collectAsStateWithLifecycle()
    val font = rememberCirculaFontFamily()

    LaunchedEffect(navEffect) {
        when (navEffect) {
            WordCountNavEffect.ToStep1 -> {
                onNavigateToStep1()
                viewModel.consumeNavEffect()
            }
            WordCountNavEffect.ToWord -> {
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
        if (state.showIntroMessage) {
            Text(
                text = "Think of a Word",
                color = PrimaryText,
                fontSize = 60.sp,
                fontFamily = font,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .entranceThenFadeOut(
                        growMillis = 1000,
                        holdMillis = 1000,
                        fadeMillis = 1000,
                        onFinished = { viewModel.onEvent(WordCountEvent.IntroFinished) }
                    )
            )
        }

        if (state.showMainContent) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 52.dp)
                    .imePadding()
                    .fadeInOnEnter()
            ) {
                Text(
                    text = "No. of Letters",
                    color = PrimaryText,
                    fontSize = 30.sp,
                    fontFamily = font
                )

                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .shake(state.shakeTrigger),
                    contentAlignment = Alignment.Center
                ) {
                    CircleBackground(size = 200.dp) {
                        BasicTextField(
                            value = state.input,
                            onValueChange = { viewModel.onEvent(WordCountEvent.InputChanged(it)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush = SolidColor(PrimaryText),
                            textStyle = TextStyle(
                                color = PrimaryText,
                                fontSize = 100.sp,
                                fontFamily = font,
                                textAlign = TextAlign.Center
                            ),
                            decorationBox = { inner ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .background(LighterTheme)
                                        .padding(horizontal = 8.dp)
                                ) {
                                    if (state.input.isEmpty()) {
                                        Text(
                                            text = "?",
                                            color = PrimaryText,
                                            fontSize = 100.sp,
                                            fontFamily = font
                                        )
                                    }
                                    inner()
                                }
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                            .size(50.dp)
                            .clickable { viewModel.onEvent(WordCountEvent.Clear) },
                        contentAlignment = Alignment.Center
                    ) {
                        CircleBackground(size = 50.dp) {
                            Text(
                                text = "X",
                                color = PrimaryText,
                                fontSize = 25.sp,
                                fontFamily = font
                            )
                        }
                    }
                }

                CircleBackground(
                    size = 80.dp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable { viewModel.onNext() }
                ) {
                    Text(
                        text = "next",
                        color = PrimaryText,
                        fontSize = 30.sp,
                        fontFamily = font
                    )
                }

                state.warningMessage?.let { warning ->
                    val message = when (warning) {
                        "empty" -> "Enter the number of letters in your word"
                        else -> "Enter a number between 1 and 26"
                    }
                    Text(
                        text = message,
                        color = PrimaryText,
                        fontSize = 16.sp,
                        fontFamily = font,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp)
                    )
                }
            }
        }

        if (state.showTutorial) {
            TutorialOverlay(
                title = "Enter the no.of letters in your word",
                detail = "and then click NEXT",
                onDismiss = { viewModel.onEvent(WordCountEvent.TutorialDismissed) }
            )
        }
    }
}
