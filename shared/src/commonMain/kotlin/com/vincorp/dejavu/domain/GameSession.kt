package com.vincorp.dejavu.domain

/**
 * In-memory session state shared across navigation destinations.
 */
class GameSession {
    var letterCount: Int = 0
        private set
    var step1Selected: List<String> = emptyList()
        private set
    var answer: String = ""
        private set

    fun startWithLetterCount(count: Int) {
        letterCount = count
        step1Selected = emptyList()
        answer = ""
    }

    fun setEasterEgg() {
        letterCount = 0
        step1Selected = emptyList()
        answer = PuzzleEngine.EASTER_EGG_ANSWER
    }

    fun completeStep1(selected: List<String>) {
        step1Selected = selected
    }

    fun completeStep2(selected: List<String>) {
        answer = PuzzleEngine.extractDiagonal(selected)
    }

    fun reset() {
        letterCount = 0
        step1Selected = emptyList()
        answer = ""
    }
}
