package com.thomaskioko.tvmaniac.testtags

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import java.io.File
import kotlin.test.Test

class IosTestTagsTest {

    @Test
    fun `every tag the ios ui tests use is declared in this module`() {
        val declared = tagsIn(sharedSource())
        val used = tagsIn(iosSource())

        withClue("Read no tags out of ${iosTagsFile().name}, so this test would pass regardless") {
            used.shouldNotBeEmpty()
        }
        withClue("Read no tags out of :core:test-tags, so this test would fail regardless") {
            declared.size shouldBeGreaterThan used.size
        }

        val undeclared = used.filterNot { it in declared }

        withClue(
            "These tags appear in ${iosTagsFile().name} but no constant in :core:test-tags has that " +
                "value. Either the shared constant was renamed and the iOS copy was not, or the iOS " +
                "copy names a tag that nothing sets:\n" + undeclared.joinToString("\n") { "  $it" },
        ) {
            undeclared.shouldBeEmpty()
        }
    }

    private fun sharedSource(): String =
        requiredDirectory("sharedTestTagsDir")
            .walkTopDown()
            .filter { it.extension == "kt" }
            .joinToString("\n") { it.readText() }

    private fun iosSource(): String = iosTagsFile().readText()

    private fun iosTagsFile(): File {
        val path = requireNotNull(System.getProperty("iosTestTagsFile")) {
            "iosTestTagsFile is not set. The test task supplies it from the build script."
        }
        return File(path).also {
            require(it.isFile) { "Expected the iOS test tags at $path" }
        }
    }

    private fun requiredDirectory(property: String): File {
        val path = requireNotNull(System.getProperty(property)) {
            "$property is not set. The test task supplies it from the build script."
        }
        return File(path).also {
            require(it.isDirectory) { "Expected a directory at $path" }
        }
    }

    private fun tagsIn(source: String): Set<String> =
        QUOTED_STRING.findAll(source)
            .map { canonical(it.groupValues[1]) }
            .toSet()

    private fun canonical(tag: String): String = buildString {
        var index = 0
        while (index < tag.length) {
            when {
                tag.startsWith("\\(", index) -> {
                    index = endOfBalancedParens(tag, index + 1)
                    append(PLACEHOLDER)
                }
                tag.startsWith("\${", index) -> {
                    index = tag.indexOf('}', index) + 1
                    append(PLACEHOLDER)
                }
                tag[index] == '$' -> {
                    index = endOfIdentifier(tag, index + 1)
                    append(PLACEHOLDER)
                }
                else -> {
                    append(tag[index])
                    index++
                }
            }
        }
    }

    private fun endOfBalancedParens(tag: String, open: Int): Int {
        var depth = 0
        var index = open
        while (index < tag.length) {
            when (tag[index]) {
                '(' -> depth++
                ')' -> if (--depth == 0) return index + 1
            }
            index++
        }
        return index
    }

    private fun endOfIdentifier(tag: String, start: Int): Int {
        var index = start
        while (index < tag.length && (tag[index].isLetterOrDigit() || tag[index] == '_')) index++
        return index
    }

    private companion object {
        val QUOTED_STRING = Regex("\"([^\"\\n]*)\"")
        const val PLACEHOLDER = "{}"
    }
}
