package com.vincorp.dejavu.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PuzzleEngineTest {

    @Test
    fun splitIntoGroups_forThreeLetters() {
        val groups = PuzzleEngine.splitIntoGroups(3)
        assertEquals(listOf("ABC", "DEF", "GHI", "JKL", "MNO", "PQR", "STU", "VWX", "YZ "), groups)
    }

    @Test
    fun transpose_andDiagonal_predictsWord() {
        // Simulate thinking of "CAT" (3 letters)
        // Step1 selections: groups containing C, A, T in order
        val groups = PuzzleEngine.splitIntoGroups(3)
        val step1 = listOf(
            groups.first { it.contains('C') }, // ABC
            groups.first { it.contains('A') }, // ABC
            groups.first { it.contains('T') }  // STU
        )
        assertEquals(listOf("ABC", "ABC", "STU"), step1)

        val transposed = PuzzleEngine.transpose(step1)
        assertEquals(listOf("AAS", "BBT", "CCU"), transposed)

        val step2 = listOf(
            transposed.first { it[0] == 'C' },
            transposed.first { it[1] == 'A' },
            transposed.first { it[2] == 'T' }
        )
        assertEquals("CAT", PuzzleEngine.extractDiagonal(step2))
    }

    @Test
    fun displayGroups_deduplicatesCharacters() {
        val display = PuzzleEngine.toDisplayGroups(listOf("AAS", "BBT", "CCU"))
        assertEquals(listOf("AS", "BT", "CU"), display)
    }

    @Test
    fun easterEggConstant() {
        assertEquals("VinCorp", PuzzleEngine.EASTER_EGG_ANSWER)
        assertTrue(PuzzleEngine.MAX_LETTER_COUNT == 26)
    }
}
