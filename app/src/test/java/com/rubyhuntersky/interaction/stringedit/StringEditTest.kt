package com.rubyhuntersky.interaction.stringedit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StringEditTest {

    private val label = Placeholder.Label<Int>("Count")
    private val validSeed = Placeholder.Seed("Count", 7, true)
    private val invalidSeed = Placeholder.Seed("Count", 8, false)
    private val validAncient = Ancient(11)
    private val invalidAncient = Ancient(12)
    private val validNovel = Novel("21", Validity.Valid(21))
    private val invalidNovel = Novel<Int>("22", Validity.Invalid("Busted"))

    @Test
    fun labelProducesNovelValueWhenNovelIsValid() {
        val tests = listOf(
            Pair(validNovel, validNovel.validValue!!),
            Pair(invalidNovel, null)
        )
        tests.forEach { (novel, expected) ->
            val editor = StringEdit(
                placeholder = label,
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
            val editor = StringEdit(
                placeholder = it.first
            )
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
            val editor = StringEdit(
                placeholder = validSeed,
                ancient = it.first
            )
            assertEquals(it.second, editor.writableValue)
        }
    }

    @Test
    fun validSeedProducesNullWhenNovelIsInvalid() {
        val editor = StringEdit(
            placeholder = validSeed,
            ancient = null,
            novel = invalidNovel
        )
        assertNull(editor.writableValue)
    }
}