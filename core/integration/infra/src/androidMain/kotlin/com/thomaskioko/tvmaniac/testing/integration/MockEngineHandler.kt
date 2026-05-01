package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.headersOf

public typealias StubResponder = suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData

public class MockEngineHandler {

    private val stubs: MutableList<Stub> = mutableListOf()

    public val jsonHeaders: Headers = headersOf("Content-Type", ContentType.Application.Json.toString())

    public fun stub(
        method: HttpMethod = HttpMethod.Get,
        path: String,
        response: StubResponder,
    ) {
        stubs += Stub.Single(method, path, response)
    }

    public fun stub(
        method: HttpMethod = HttpMethod.Get,
        path: String,
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) {
        stubs += Stub.Single(method, path) { _ ->
            respond(
                content = body,
                status = status,
                headers = jsonHeaders,
            )
        }
    }

    public fun stubFixture(
        method: HttpMethod = HttpMethod.Get,
        path: String,
        fixturePath: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) {
        stubs += Stub.Single(method, path) { _ ->
            respond(
                content = FixtureLoader.load(fixturePath),
                status = status,
                headers = jsonHeaders,
            )
        }
    }

    public fun stubSequence(
        method: HttpMethod = HttpMethod.Get,
        path: String,
        block: SequenceBuilder.() -> Unit,
    ) {
        val builder = SequenceBuilder().apply(block)
        stubs += Stub.Sequence(method, path, builder.responses)
    }

    public fun stubByQuery(
        method: HttpMethod = HttpMethod.Get,
        path: String,
        fixtureSelector: (Parameters) -> String?,
    ) {
        stubs += Stub.ByQuery(method, path, fixtureSelector)
    }

    public fun stubPattern(
        method: HttpMethod = HttpMethod.Get,
        pathRegex: String,
        response: StubResponder,
    ) {
        stubs += Stub.PathPattern(method, pathRegex.toRegex(), response)
    }

    public fun stubPattern(
        method: HttpMethod = HttpMethod.Get,
        pathRegex: String,
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) {
        stubs += Stub.PathPattern(method, pathRegex.toRegex()) { _ ->
            respond(
                content = body,
                status = status,
                headers = jsonHeaders,
            )
        }
    }

    public fun stubPatternFixture(
        method: HttpMethod = HttpMethod.Get,
        pathRegex: String,
        fixturePath: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) {
        stubs += Stub.PathPattern(method, pathRegex.toRegex()) { _ ->
            respond(
                content = FixtureLoader.load(fixturePath),
                status = status,
                headers = jsonHeaders,
            )
        }
    }

    public fun reset() {
        stubs.clear()
    }

    public suspend fun handle(
        scope: MockRequestHandleScope,
        request: HttpRequestData,
        printLogs: Boolean = false,
    ): HttpResponseData {
        val requestPath = request.url.encodedPath

        stubs.asReversed().forEach { stub ->
            if (stub.method != request.method || !stub.matches(requestPath)) return@forEach
            when (stub) {
                is Stub.Single -> return stub.response(scope, request)
                is Stub.Sequence -> {
                    val next = stub.responses.removeFirstOrNull()
                        ?: error(
                            "Stub sequence for ${stub.method.value} ${stub.path} exhausted after matching request: " +
                                "${request.method.value} ${request.url}",
                        )
                    return next(scope, request)
                }
                is Stub.ByQuery -> {
                    val fixturePath = stub.fixtureSelector(request.url.parameters)
                    if (fixturePath != null) {
                        return scope.respond(
                            content = FixtureLoader.load(fixturePath),
                            status = HttpStatusCode.OK,
                            headers = jsonHeaders,
                        )
                    }
                }
                is Stub.PathPattern -> return stub.response(scope, request)
            }
        }

        error(
            buildString {
                appendLine("No stub registered for request: ${request.method.value} ${request.url}")
                appendLine("Registered stubs (last-registered-wins):")
                if (stubs.isEmpty()) {
                    appendLine("  (none)")
                } else {
                    stubs.asReversed().forEach { append("  - ").appendLine(it.describe()) }
                }
            },
        )
    }

    public inner class SequenceBuilder internal constructor() {
        internal val responses: MutableList<StubResponder> = mutableListOf()

        public fun respondFixture(
            fixturePath: String,
            status: HttpStatusCode = HttpStatusCode.OK,
        ) {
            responses += { _ ->
                respond(
                    content = FixtureLoader.load(fixturePath),
                    status = status,
                    headers = jsonHeaders,
                )
            }
        }

        public fun respondError(
            status: HttpStatusCode,
            body: String = "",
        ) {
            responses += { _ ->
                respond(content = body, status = status)
            }
        }

        public fun respond(
            content: String,
            status: HttpStatusCode,
            contentType: ContentType = ContentType.Application.Json,
        ) {
            responses += { _ ->
                respond(
                    content = content,
                    status = status,
                    headers = headersOf("Content-Type", contentType.toString()),
                )
            }
        }
    }

    private sealed interface Stub {
        val method: HttpMethod
        val path: String

        fun matches(requestPath: String): Boolean = path == requestPath

        fun describe(): String = "${method.value} $path (${this::class.simpleName})"

        data class Single(
            override val method: HttpMethod,
            override val path: String,
            val response: StubResponder,
        ) : Stub

        data class Sequence(
            override val method: HttpMethod,
            override val path: String,
            val responses: MutableList<StubResponder>,
        ) : Stub

        data class ByQuery(
            override val method: HttpMethod,
            override val path: String,
            val fixtureSelector: (Parameters) -> String?,
        ) : Stub

        data class PathPattern(
            override val method: HttpMethod,
            val pathRegex: Regex,
            val response: StubResponder,
        ) : Stub {
            override val path: String get() = pathRegex.pattern
            override fun matches(requestPath: String): Boolean = pathRegex.matches(requestPath)
        }
    }

    public companion object {
        public val handler: MockEngineHandler = MockEngineHandler()
    }
}
