package com.thevinesh.dejavu.screens.word

import androidx.lifecycle.ViewModel
import com.thevinesh.dejavu.data.TutorialPreferences
import com.thevinesh.dejavu.domain.GameSession
import com.thevinesh.dejavu.platform.SoundPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WordUiState(
    val answer: String = "",
    val zoomFinished: Boolean = false,
    val physicsEnabled: Boolean = false,
    val showPlayLabel: Boolean = false
)

class WordViewModel(
    gameSession: GameSession,
    private val tutorialPreferences: TutorialPreferences,
    private val soundPlayer: SoundPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WordUiState(answer = gameSession.answer)
    )
    val uiState: StateFlow<WordUiState> = _uiState.asStateFlow()

    init {
        tutorialPreferences.markTutorialSeen()
    }

    fun onZoomFinished() {
        _uiState.update {
            it.copy(zoomFinished = true, showPlayLabel = true)
        }
    }

    fun onPlayTapped() {
        _uiState.update {
            it.copy(physicsEnabled = true, showPlayLabel = false)
        }
    }

    fun onCollision() {
        soundPlayer.playBounce()
    }
}
