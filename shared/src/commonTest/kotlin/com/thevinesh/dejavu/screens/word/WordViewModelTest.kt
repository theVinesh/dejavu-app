package com.thevinesh.dejavu.screens.word

import com.thevinesh.dejavu.data.TutorialPreferences
import com.thevinesh.dejavu.domain.GameSession
import com.thevinesh.dejavu.platform.ShareLauncher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class WordViewModelTest {

    @Test
    fun feedbackLocksOppositeChoiceButReplaysSelectedChoice() {
        val session = GameSession().apply { setEasterEgg() }
        val preferences = TutorialPreferences().apply { isFirstTime = false }
        val shareLauncher = RecordingShareLauncher()
        val viewModel = WordViewModel(session, preferences, shareLauncher)

        viewModel.onFeedback(RevealFeedback.Positive)
        assertEquals(RevealFeedback.Positive, viewModel.uiState.value.feedback)
        assertEquals(1, viewModel.uiState.value.feedbackAnimationId)

        viewModel.onFeedback(RevealFeedback.Negative)
        assertEquals(RevealFeedback.Positive, viewModel.uiState.value.feedback)
        assertEquals(1, viewModel.uiState.value.feedbackAnimationId)

        viewModel.onFeedback(RevealFeedback.Positive)
        assertEquals(RevealFeedback.Positive, viewModel.uiState.value.feedback)
        assertEquals(2, viewModel.uiState.value.feedbackAnimationId)
    }

    @Test
    fun shareSendsCatchyCopyWithStoreLinkAndWithholdsWord() {
        val session = GameSession().apply { setEasterEgg() }
        val preferences = TutorialPreferences().apply { isFirstTime = false }
        val shareLauncher = RecordingShareLauncher()
        val viewModel = WordViewModel(session, preferences, shareLauncher)

        viewModel.onShare()

        val shared = shareLauncher.shared.single()
        assertEquals(
            "I thought of a word and DejaVu read my mind. No setup, no trick. It just knew. Your turn: https://vineshbuilds.app/dejavu",
            shared
        )
        assertFalse(
            shared.contains(session.answer),
            "Share text must not reveal the guessed word"
        )
    }

    private class RecordingShareLauncher : ShareLauncher {
        val shared = mutableListOf<String>()
        override fun shareText(text: String) {
            shared += text
        }
    }
}
