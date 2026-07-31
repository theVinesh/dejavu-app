package com.thevinesh.dejavu.screens.step

import com.thevinesh.dejavu.data.TutorialPreferences
import com.thevinesh.dejavu.domain.GameSession
import kotlin.test.Test
import kotlin.test.assertEquals

class StepViewModelTest {

    @Test
    fun repeatedSelectionsExposeStackedOrderBadgesAndUndoInReverse() {
        val session = GameSession().apply { startWithLetterCount(3) }
        val preferences = TutorialPreferences().apply {
            isFirstTime = false
        }
        val viewModel = StepViewModel(session, preferences)

        viewModel.startStep1()
        viewModel.onGroupClick(0)
        viewModel.onGroupClick(0)
        viewModel.onGroupClick(6)

        assertEquals("ABC", viewModel.uiState.value.groups[0].letters)
        assertEquals(listOf(1, 2), viewModel.uiState.value.groups[0].selectionOrders)
        assertEquals(listOf(3), viewModel.uiState.value.groups[6].selectionOrders)

        viewModel.onUndo()
        assertEquals(emptyList(), viewModel.uiState.value.groups[6].selectionOrders)
        assertEquals(listOf(1, 2), viewModel.uiState.value.groups[0].selectionOrders)

        viewModel.onUndo()
        assertEquals(listOf(1), viewModel.uiState.value.groups[0].selectionOrders)
    }
}
