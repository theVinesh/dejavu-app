package com.vincorp.dejavu.screens.wordcount

import androidx.lifecycle.ViewModel
import com.vincorp.dejavu.data.TutorialPreferences
import com.vincorp.dejavu.domain.GameSession
import com.vincorp.dejavu.domain.PuzzleEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WordCountUiState(
    val input: String = "",
    val showIntroMessage: Boolean = true,
    val showMainContent: Boolean = false,
    val showTutorial: Boolean = false,
    val shakeTrigger: Int = 0,
    val warningMessage: String? = null
)

sealed interface WordCountEvent {
    data object IntroFinished : WordCountEvent
    data object TutorialDismissed : WordCountEvent
    data class InputChanged(val value: String) : WordCountEvent
    data object Clear : WordCountEvent
    data object WarningShown : WordCountEvent
}

sealed interface WordCountNavEffect {
    data object ToStep1 : WordCountNavEffect
    data object ToWord : WordCountNavEffect
}

class WordCountViewModel(
    private val gameSession: GameSession,
    private val tutorialPreferences: TutorialPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WordCountUiState(showTutorial = false)
    )
    val uiState: StateFlow<WordCountUiState> = _uiState.asStateFlow()

    private val _navEffect = MutableStateFlow<WordCountNavEffect?>(null)
    val navEffect: StateFlow<WordCountNavEffect?> = _navEffect.asStateFlow()

    fun onEvent(event: WordCountEvent) {
        when (event) {
            WordCountEvent.IntroFinished -> {
                _uiState.update {
                    it.copy(
                        showIntroMessage = false,
                        showMainContent = true,
                        showTutorial = tutorialPreferences.isFirstTime
                    )
                }
            }

            WordCountEvent.TutorialDismissed -> {
                _uiState.update { it.copy(showTutorial = false) }
            }

            is WordCountEvent.InputChanged -> {
                val isValidEdit = event.value.length <= 2 &&
                    event.value.all { it in '0'..'9' }
                if (isValidEdit) {
                    _uiState.update {
                        it.copy(input = event.value, warningMessage = null)
                    }
                }
            }

            WordCountEvent.Clear -> {
                _uiState.update { it.copy(input = "", warningMessage = null) }
            }

            WordCountEvent.WarningShown -> {
                _uiState.update { it.copy(warningMessage = null) }
            }
        }
    }

    fun onNext() {
        val raw = _uiState.value.input.trim()
        if (raw.isEmpty()) {
            _uiState.update {
                it.copy(
                    shakeTrigger = it.shakeTrigger + 1,
                    warningMessage = "empty"
                )
            }
            return
        }

        val count = raw.toIntOrNull()
        if (count == null) {
            _uiState.update {
                it.copy(
                    shakeTrigger = it.shakeTrigger + 1,
                    warningMessage = "invalid"
                )
            }
            return
        }

        if (count == 0) {
            gameSession.setEasterEgg()
            _navEffect.value = WordCountNavEffect.ToWord
            return
        }

        if (count !in PuzzleEngine.MIN_LETTER_COUNT..PuzzleEngine.MAX_LETTER_COUNT) {
            _uiState.update {
                it.copy(
                    shakeTrigger = it.shakeTrigger + 1,
                    warningMessage = "invalid"
                )
            }
            return
        }

        gameSession.startWithLetterCount(count)
        _navEffect.value = WordCountNavEffect.ToStep1
    }

    fun consumeNavEffect() {
        _navEffect.value = null
    }
}
