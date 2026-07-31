package com.thevinesh.dejavu.screens.step

import androidx.lifecycle.ViewModel
import com.thevinesh.dejavu.data.TutorialPreferences
import com.thevinesh.dejavu.domain.GameSession
import com.thevinesh.dejavu.domain.PuzzleEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class StepPhase { Step1, Step2 }

data class GroupItem(
    val letters: String,
    val selectionOrder: Int? = null
)

data class StepUiState(
    val phase: StepPhase = StepPhase.Step1,
    val groups: List<GroupItem> = emptyList(),
    val selectedCount: Int = 0,
    val requiredCount: Int = 0,
    val canUndo: Boolean = false,
    val showNext: Boolean = false,
    val showTutorial: Boolean = false,
    val showTapHint: Boolean = false,
    val shakeTrigger: Int = 0,
    val tutorialTitle: String = "",
    val tutorialDetail: String = ""
)

sealed interface StepNavEffect {
    data object ToStep2 : StepNavEffect
    data object ToWord : StepNavEffect
}

class StepViewModel(
    private val gameSession: GameSession,
    private val tutorialPreferences: TutorialPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(StepUiState())
    val uiState: StateFlow<StepUiState> = _uiState.asStateFlow()

    private val _navEffect = MutableStateFlow<StepNavEffect?>(null)
    val navEffect: StateFlow<StepNavEffect?> = _navEffect.asStateFlow()

    private var sourceGroups: List<String> = emptyList()
    private val selectedGroups = mutableListOf<String>()
    private val selectionIndexStack = ArrayDeque<Int>()

    fun startStep1() {
        val count = gameSession.letterCount
        sourceGroups = PuzzleEngine.splitIntoGroups(count)
        selectedGroups.clear()
        selectionIndexStack.clear()
        val firstTime = tutorialPreferences.isFirstTime
        _uiState.value = StepUiState(
            phase = StepPhase.Step1,
            groups = sourceGroups.map { GroupItem(it) },
            requiredCount = count,
            showTutorial = firstTime,
            showTapHint = firstTime,
            tutorialTitle = "Tap the groups in which\nthe letters occur",
            tutorialDetail = "in the order as they occur"
        )
    }

    fun startStep2() {
        sourceGroups = PuzzleEngine.transpose(gameSession.step1Selected)
        selectedGroups.clear()
        selectionIndexStack.clear()
        val display = PuzzleEngine.toDisplayGroups(sourceGroups)
        val firstTime = tutorialPreferences.isFirstTime
        _uiState.value = StepUiState(
            phase = StepPhase.Step2,
            groups = display.map { GroupItem(it) },
            requiredCount = gameSession.letterCount,
            showTutorial = firstTime,
            showTapHint = firstTime,
            tutorialTitle = "Again!, One more time",
            tutorialDetail = "Tap the groups in which\nthe letters occur, in the order as they occur"
        )
    }

    fun onGroupClick(index: Int) {
        val state = _uiState.value
        if (selectedGroups.size >= state.requiredCount) return
        if (index !in sourceGroups.indices) return

        selectedGroups += sourceGroups[index]
        selectionIndexStack.addLast(index)
        rebuildDisplay()
    }

    fun onUndo() {
        if (selectedGroups.isEmpty()) {
            _uiState.update { it.copy(shakeTrigger = it.shakeTrigger + 1) }
            return
        }
        selectedGroups.removeAt(selectedGroups.lastIndex)
        selectionIndexStack.removeLast()
        rebuildDisplay()
    }

    fun onNext() {
        val state = _uiState.value
        if (selectedGroups.size != state.requiredCount) return
        when (state.phase) {
            StepPhase.Step1 -> {
                gameSession.completeStep1(selectedGroups.toList())
                _navEffect.value = StepNavEffect.ToStep2
            }
            StepPhase.Step2 -> {
                gameSession.completeStep2(selectedGroups.toList())
                _navEffect.value = StepNavEffect.ToWord
            }
        }
    }

    fun dismissTutorial() {
        _uiState.update {
            it.copy(showTutorial = false, showTapHint = false)
        }
    }

    fun consumeNavEffect() {
        _navEffect.value = null
    }

    private fun rebuildDisplay() {
        // Original allows selecting the same cell multiple times and appends "(n)" each time.
        val displayBase = if (_uiState.value.phase == StepPhase.Step2) {
            PuzzleEngine.toDisplayGroups(sourceGroups)
        } else {
            sourceGroups.map { it.trimEnd() }
        }

        val orderMarkers = MutableList(displayBase.size) { mutableListOf<Int>() }
        selectionIndexStack.forEachIndexed { order, groupIndex ->
            orderMarkers[groupIndex] += order + 1
        }

        val groups = displayBase.mapIndexed { index, letters ->
            val markers = orderMarkers[index]
            val label = if (markers.isEmpty()) {
                letters
            } else {
                letters + markers.joinToString(separator = "") { "($it)" }
            }
            GroupItem(
                letters = label,
                selectionOrder = markers.lastOrNull()
            )
        }

        _uiState.update {
            it.copy(
                groups = groups,
                selectedCount = selectedGroups.size,
                canUndo = selectedGroups.isNotEmpty(),
                showNext = selectedGroups.size == it.requiredCount
            )
        }
    }
}
