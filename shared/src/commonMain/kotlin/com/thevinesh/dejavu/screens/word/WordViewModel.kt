package com.thevinesh.dejavu.screens.word

import androidx.lifecycle.ViewModel
import com.thevinesh.dejavu.data.TutorialPreferences
import com.thevinesh.dejavu.domain.GameSession
import com.thevinesh.dejavu.platform.DEJAVU_SHARE_TEXT
import com.thevinesh.dejavu.platform.ShareLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class RevealFeedback {
    None,
    Positive,
    Negative
}

data class WordUiState(
    val answer: String = "",
    val zoomFinished: Boolean = false,
    val feedback: RevealFeedback = RevealFeedback.None
)

class WordViewModel(
    gameSession: GameSession,
    private val tutorialPreferences: TutorialPreferences,
    private val shareLauncher: ShareLauncher
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WordUiState(answer = gameSession.answer)
    )
    val uiState: StateFlow<WordUiState> = _uiState.asStateFlow()

    init {
        tutorialPreferences.markTutorialSeen()
    }

    fun onZoomFinished() {
        _uiState.update { it.copy(zoomFinished = true) }
    }

    fun onFeedback(feedback: RevealFeedback) {
        if (feedback == RevealFeedback.None) return
        if (_uiState.value.feedback != RevealFeedback.None) return
        _uiState.update { it.copy(feedback = feedback) }
    }

    fun onShare() {
        shareLauncher.shareText(DEJAVU_SHARE_TEXT)
    }
}
