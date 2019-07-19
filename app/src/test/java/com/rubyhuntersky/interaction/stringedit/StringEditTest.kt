package com.rubyhuntersky.interaction.stringedit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StringEditTest {

    private val label = "Count"
    private val validSeed = Seed(7, true)
    private val invalidSeed = Seed(8, false)
    private val validAncient = Ancient(11)
    private val invalidAncient = Ancient(12)
    private val validNovel = Novel("21", Validity.Valid(21))
    private val invalidNovel = Novel<Int>("22", Validity.Invalid("22", "Busted"))

    @Test
    fun labelProducesNovelValueWhenNovelIsValid() {
        val tests = listOf(
            Pair(validNovel, validNovel.validValue!!),
            Pair(invalidNovel, null)
        )
        tests.forEach { (novel, expected) ->
            val editor = StringEdit(
                label,
                seed = null,
                ancient = null,
                novel = novel
            )
            assertEquals(expected, editor.writableValue)
        }
    }

    @Test
    fun seedProducesSeedValueWhenSeedIsValid() {
        val tests = listOf(
            Pair(validSeed, validSeed.value),
            Pair(invalidSeed, null)
        )
        tests.forEach {
            val editor = StringEdit(label, seed = it.first)
            assertEquals(it.second, editor.writableValue)
        }
    }

    @Test
    fun anyAncientWithNullNovelProducesNullEvenWhenSeedIsValid() {
        val tests = listOf(
            Pair(null, validSeed.value),
            Pair(invalidAncient, null),
            Pair(validAncient, null)
        )
        tests.forEach {
            val editor = StringEdit(label, validSeed, ancient = it.first)
            assertEquals(it.second, editor.writableValue)
        }
    }

    @Test
    fun validSeedProducesNullWhenNovelIsInvalid() {
        val editor = StringEdit(label, validSeed, ancient = null, novel = invalidNovel)
        assertNull(editor.writableValue)
    }
}