package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

/**
 * Stubs the typed-search endpoint (`/search?type=show&query=<query>&extended=full`) using the
 * fixture for [status] when the incoming request's `query` parameter matches [query].
 */
public fun MockEngineHandler.stubSearchByQuery(query: String, status: HttpStatusCode = HttpStatusCode.OK) {
    stub(path = Endpoints.Trakt.Search.path) { request ->
        if (request.url.parameters["query"] != query) {
            error("No stub match for search query '${request.url.parameters["query"]}'. Expected '$query'.")
        }
        val fixture = if (status.isSuccess()) Endpoints.Trakt.Search.successFixture else Endpoints.Trakt.Search.errorFixture
        respond(
            content = FixtureLoader.load(fixture),
            status = status,
            headers = jsonHeaders,
        )
    }
}
