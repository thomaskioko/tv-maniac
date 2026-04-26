package com.thomaskioko.tvmaniac.testing.integration

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class MockEngineHandlerTest {

    @Test
    fun `should return fixture content when stub path matches`() = runTest {
        val handler = MockEngineHandler()
        handler.stubFixture(path = "/test", fixturePath = "test/hello.json")
        val client = mockClient(handler)

        val body = client.get("/test").bodyAsText()

        body shouldContain "hello from the fixture"
    }

    @Test
    fun `should match method alongside path`() = runTest {
        val handler = MockEngineHandler()
        handler.stub(method = HttpMethod.Post, path = "/write") {
            respond("posted", HttpStatusCode.OK)
        }
        val client = mockClient(handler)

        val body = client.post("/write").bodyAsText()

        body shouldBe "posted"
    }

    @Test
    fun `should throw descriptive error when no stub matches`() = runTest {
        val handler = MockEngineHandler()
        handler.stubFixture(path = "/known", fixturePath = "test/hello.json")
        val client = mockClient(handler)

        val error = shouldThrow<IllegalStateException> {
            client.get("/unexpected?page=2")
        }

        error.message shouldContain "GET"
        error.message shouldContain "/unexpected"
        error.message shouldContain "page=2"
        error.message shouldContain "/known"
    }

    @Test
    fun `should prefer last-registered stub when paths overlap`() = runTest {
        val handler = MockEngineHandler()
        handler.stub(path = "/same") { respond("first", HttpStatusCode.OK) }
        handler.stub(path = "/same") { respond("second", HttpStatusCode.OK) }
        val client = mockClient(handler)

        val body = client.get("/same").bodyAsText()

        body shouldBe "second"
    }

    @Test
    fun `should clear stubs on reset`() = runTest {
        val handler = MockEngineHandler()
        handler.stubFixture(path = "/test", fixturePath = "test/hello.json")
        val client = mockClient(handler)

        handler.reset()

        shouldThrow<IllegalStateException> {
            client.get("/test")
        }
    }

    @Test
    fun `should consume sequence responses FIFO`() = runTest {
        val handler = MockEngineHandler()
        handler.stubSequence(path = "/retry") {
            respondError(HttpStatusCode.ServiceUnavailable, body = "oops")
            respond(content = "ok", status = HttpStatusCode.OK)
        }
        val client = mockClient(handler)

        val first = client.get("/retry")
        val second = client.get("/retry")

        first.status shouldBe HttpStatusCode.ServiceUnavailable
        second.status shouldBe HttpStatusCode.OK
        second.bodyAsText() shouldBe "ok"
    }

    @Test
    fun `should throw when sequence is exhausted`() = runTest {
        val handler = MockEngineHandler()
        handler.stubSequence(path = "/once") {
            respond(content = "only", status = HttpStatusCode.OK)
        }
        val client = mockClient(handler)

        client.get("/once")

        val error = shouldThrow<IllegalStateException> {
            client.get("/once")
        }
        error.message shouldContain "exhausted"
    }

    @Test
    fun `should route by query parameter`() = runTest {
        val handler = MockEngineHandler()
        handler.stubByQuery(path = "/lookup") { params ->
            when (params["id"]) {
                "1" -> "test/hello.json"
                else -> null
            }
        }
        val client = mockClient(handler)

        val body = client.get("/lookup?id=1").bodyAsText()

        body shouldContain "hello from the fixture"
    }

    @Test
    fun `should fall through to unmatched when query selector returns null`() = runTest {
        val handler = MockEngineHandler()
        handler.stubByQuery(path = "/lookup") { params ->
            if (params["id"] == "1") "test/hello.json" else null
        }
        val client = mockClient(handler)

        shouldThrow<IllegalStateException> {
            client.get("/lookup?id=2")
        }
    }

    private fun mockClient(handler: MockEngineHandler): HttpClient =
        HttpClient(MockEngine { request -> handler.handle(this, request) })
}
