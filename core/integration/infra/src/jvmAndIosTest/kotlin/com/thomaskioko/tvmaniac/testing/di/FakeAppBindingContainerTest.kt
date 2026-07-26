package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

/**
 * No presenter test would catch these providers reverting to `respond("{}")`, because none of them
 * make HTTP calls.
 */
class FakeAppBindingContainerTest {

    @AfterTest
    fun tearDown() {
        MockEngineHandler.handler.reset()
    }

    @Test
    fun `should reject an unstubbed request given the graph's engine`() = runTest {
        val client = HttpClient(FakeAppBindingContainer.provideTmdbHttpClientEngine())

        val error = shouldThrowAny {
            client.get("https://api.themoviedb.org/3/tv/1")
        }

        error.message shouldContain "No stub registered for"
    }

    @Test
    fun `should answer from the shared handler given a registered stub`() = runTest {
        MockEngineHandler.handler.stub(
            path = "/3/tv/1",
            body = """{"id":1}""",
            status = HttpStatusCode.OK,
        )
        val client = HttpClient(FakeAppBindingContainer.provideTraktHttpClientEngine())

        client.get("https://api.themoviedb.org/3/tv/1").bodyAsText() shouldContain "\"id\":1"
    }
}
