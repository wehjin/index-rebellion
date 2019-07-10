package com.rubyhuntersky.interaction.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EditorTest {

    private val label = Placeholder.Label<Int>("Count")
    private val validSeed = Placeholder.Seed(7, true, "Count")
    private val invalidSeed = Placeholder.Seed(8, false, "Count")
    private val validAncient = Ancient(11, true)
    private val invalidAncient = Ancient(12, false)
    private val validNovel = Novel(21, Validity.Valid)
    private val invalidNovel = Novel(22, Validity.Invalid("Busted"))
    private val allPlaceholders = listOf(label, validSeed, invalidSeed)
    private val allAncients = listOf(null, validAncient, invalidAncient)

    @Test
    fun strictLabelProducesNovelValueWhenNovelIsValid() {
        val tests = listOf(
            Pair(validNovel, validNovel.value),
            Pair(invalidNovel, null)
        )
        tests.forEach { (novel, expected) ->
            val editor = Editor(
                isStrict = true,
                placeholder = label,
                novel = novel
            )
            assertEquals(expected, editor.writableValue)
        }
    }

    @Test
    fun strictSeedProducesSeedValueWhenSeedIsValid() {
        val tests = listOf(
            Pair(validSeed, validSeed.value),
            Pair(invalidSeed, null)
        )
        tests.forEach {
            val editor = Editor(
                isStrict = true,
                placeholder = it.first
            )
            assertEquals(it.second, editor.writableValue)
        }
    }

    @Test
    fun strictValidSeedProducesSeedValueOnlyWhenNoAncient() {
        val tests = listOf(
            Pair(null, validSeed.value),
            Pair(invalidAncient, null),
            Pair(validAncient, null)
        )
        tests.forEach {
            val editor = Editor(
                isStrict = true,
                placeholder = validSeed,
                ancient = it.first
            )
            assertEquals(it.second, editor.writableValue)
        }
    }

    @Test
    fun strictValidSeedProducesNullWhenNovelIsInvalid() {
        val editor = Editor(
            isStrict = true,
            placeholder = validSeed,
            ancient = null,
            novel = Novel(9, Validity.Invalid("Wrong count"))
        )
        assertNull(editor.writableValue)
    }

    @Test
    fun looseProducesSeedValueEvenWhenSeedIsInvalidAndNoNovelNoAncient() {
        val tests = listOf(
            Pair(label, null),
            Pair(validSeed, validSeed.value),
            Pair(invalidSeed, invalidSeed.value)
        )
        tests.forEach { (placeholder, expected) ->
            val editor = Editor(
                isStrict = false,
                placeholder = placeholder,
                ancient = null,
                novel = null
            )
            assertEquals(expected, editor.writableValue)
        }
    }

    @Test
    fun looseNoAncientProducesNovelValueEvenWhenNovelIsInvalidAndSeedValueWhenNovelIsNull() {
        allPlaceholders.forEach { placeholder ->
            val tests = listOf(
                Pair(validNovel, validNovel.value),
                Pair(invalidNovel, invalidNovel.value),
                Pair(null, (placeholder as? Placeholder.Seed)?.value)
            )
            tests.forEach { (novel, expected) ->
                val editor = Editor(
                    isStrict = false,
                    placeholder = placeholder,
                    ancient = null,
                    novel = novel
                )
                assertEquals("$editor", expected, editor.writableValue)
            }
        }
    }
}