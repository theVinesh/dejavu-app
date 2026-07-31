package com.vincorp.dejavu.domain

/**
 * Core DejaVu word-prediction algorithm:
 * 1. Split alphabet into groups of [letterCount] letters
 * 2. User selects [letterCount] groups (with replacement) in letter order
 * 3. Transpose columns of the selected groups into new groups
 * 4. User selects again; the diagonal of those selections is the word
 */
object PuzzleEngine {
    const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val EASTER_EGG_ANSWER = "VinCorp"
    const val MIN_LETTER_COUNT = 1
    const val MAX_LETTER_COUNT = 26

    fun splitIntoGroups(letterCount: Int): List<String> {
        require(letterCount in MIN_LETTER_COUNT..MAX_LETTER_COUNT) {
            "letterCount must be between $MIN_LETTER_COUNT and $MAX_LETTER_COUNT"
        }

        val groups = mutableListOf<String>()
        var index = 0
        val fullGroupsEnd = 26 - (26 % letterCount)
        while (index < fullGroupsEnd) {
            groups += ALPHABET.substring(index, index + letterCount)
            index += letterCount
        }
        if (index < 26) {
            val remaining = ALPHABET.substring(index)
            groups += remaining.padEnd(letterCount, ' ')
        }
        return groups
    }

    /** Transpose selected groups: column i becomes a new group. */
    fun transpose(selectedGroups: List<String>): List<String> {
        val count = selectedGroups.size
        require(count > 0)
        return List(count) { column ->
            buildString {
                for (row in 0 until count) {
                    append(selectedGroups[row].getOrElse(column) { ' ' })
                }
            }
        }
    }

    /** Unique characters per group for Step 2 display (matches original UX). */
    fun toDisplayGroups(groups: List<String>): List<String> {
        return groups.map { group ->
            buildString {
                for (char in group) {
                    if (!contains(char)) append(char)
                }
            }
        }
    }

    /** Diagonal extraction: selected[i][i] for each position. */
    fun extractDiagonal(selectedGroups: List<String>): String {
        return buildString {
            for (i in selectedGroups.indices) {
                append(selectedGroups[i].getOrElse(i) { ' ' })
            }
        }
    }

    fun formatGroupLabel(letters: String, selectionOrder: Int?): String {
        val trimmed = letters.trimEnd()
        return if (selectionOrder != null) {
            "$trimmed\n($selectionOrder)"
        } else {
            trimmed
        }
    }
}
