package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

internal class FixtureLoaderTest {

    @Test
    fun `should load existing fixture from classpath`() {
        val content = FixtureLoader.load("test/hello.json")

        content shouldContain "hello from the fixture"
    }

    @Test
    fun `should throw with descriptive message when fixture is missing`() {
        val error = shouldThrow<IllegalStateException> {
            FixtureLoader.load("test/does_not_exist.json")
        }

        error.message shouldContain "fixtures/test/does_not_exist.json"
    }
}
