package com.thevinesh.dejavu.screens.word

import com.thevinesh.dejavu.data.TutorialPreferences
import com.thevinesh.dejavu.domain.GameSession
import com.thevinesh.dejavu.platform.ShareLauncher
import kotlin.test.Test
import kotlin.test.assertEquals

class WordViewModelTest {

    @Test
    fun feedbackLocksAfterFirstChoice() {
        val session = GameSession().apply { setEasterEgg() }
        val preferences = TutorialPreferences().apply { isFirstTime = false }
        val shareLauncher = RecordingShareLauncher()
        val viewModel = WordViewModel(session, preferences, shareLauncher)

        viewModel.onFeedback(RevealFeedback.Positive)
        assertEquals(RevealFeedback.Positive, viewModel.uiState.value.feedback)

        viewModel.onFeedback(RevealFeedback.Negative)
        assertEquals(RevealFeedback.Positive, viewModel.uiState.value.feedback)
    }

    @Test
    fun shareUsesPlaceholderLink() {
        val session = GameSession().apply { setEasterEgg() }
        val preferences = TutorialPreferences().apply { isFirstTime = false }
        val shareLauncher = RecordingShareLauncher()
        val viewModel = WordViewModel(session, preferences, shareLauncher)

        viewModel.onShare()

        assertEquals(listOf("https://vineshbuilds.app/dejavu"), shareLauncher.shared)
    }

    private class RecordingShareLauncher : ShareLauncher {
        val shared = mutableListOf<String>()
        override fun shareText(text: String) {
            shared += text
        }
    }
}
