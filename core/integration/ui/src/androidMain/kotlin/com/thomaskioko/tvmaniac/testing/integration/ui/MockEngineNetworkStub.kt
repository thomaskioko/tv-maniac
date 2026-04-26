package com.thomaskioko.tvmaniac.testing.integration.ui

import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

/**
 * [NetworkStub] backed by Ktor [io.ktor.client.engine.mock.MockEngine] for Trakt and TMDB clients.
 *
 * Paths starting with `/3/` are forwarded to TMDB engine; others go to Trakt. Fixture responses
 * are resolved through [fixtureReader].
 *
 * @property traktMock Mock engine handler for Trakt client.
 * @property tmdbMock Mock engine handler for TMDB client.
 * @property fixtureReader Reader used to resolve [NetworkResponse.Fixture] bodies.
 */
public class MockEngineNetworkStub(
    private val traktMock: MockEngineHandler,
    private val tmdbMock: MockEngineHandler,
    private val fixtureReader: FixtureReader,
) : NetworkStub {

    private val jsonHeaders = headersOf("Content-Type", ContentType.Application.Json.toString())

    override fun stub(path: String, response: NetworkResponse) {
        val mock = if (path.startsWith("/3/")) tmdbMock else traktMock

        when (response) {
            is NetworkResponse.Success -> mock.stub(path = path) {
                respond(
                    content = response.body,
                    status = HttpStatusCode.fromValue(200),
                    headers = jsonHeaders,
                )
            }
            is NetworkResponse.Error -> mock.stub(path = path) {
                respond(
                    content = response.body,
                    status = HttpStatusCode.fromValue(response.status),
                    headers = jsonHeaders,
                )
            }
            is NetworkResponse.Fixture -> mock.stubFixture(
                path = path,
                fixturePath = response.path,
            )
        }
    }

    override fun stubByQuery(path: String, selector: (Map<String, String>) -> NetworkResponse?) {
        val mock = if (path.startsWith("/3/")) tmdbMock else traktMock
        mock.stub(path = path) { request ->
            val parameters = request.url.parameters
            val map = parameters.entries().associate { it.key to it.value.first() }
            val response = selector(map) ?: error("No stub match for query parameters: $map")

            when (response) {
                is NetworkResponse.Success -> respond(
                    content = response.body,
                    status = HttpStatusCode.fromValue(200),
                    headers = jsonHeaders,
                )
                is NetworkResponse.Error -> respond(
                    content = response.body,
                    status = HttpStatusCode.fromValue(response.status),
                    headers = jsonHeaders,
                )
                is NetworkResponse.Fixture -> respond(
                    content = fixtureReader.read(response.path),
                    status = HttpStatusCode.OK,
                    headers = jsonHeaders,
                )
            }
        }
    }
}
