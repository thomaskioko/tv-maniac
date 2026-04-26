package com.thomaskioko.tvmaniac.testing.integration.ui

import androidx.compose.ui.test.junit4.ComposeContentTestRule

/**
 * Integration test environment for running tests on different runners (Robolectric, Emulator, etc.).
 *
 * @param G Metro graph type exposed to the test.
 */
public interface IntegrationTestEnvironment<G> {
    /** Compose rule driving the activity under test. */
    public val composeTestRule: ComposeContentTestRule

    /** Dependency graph the test inspects or injects into. */
    public val graph: G

    /** Network stubber for wiring mock responses before composition. */
    public val stubber: NetworkStub
}

/**
 * Interface for network stubbing. Hides the underlying engine so tests depend only on path and
 * response semantics.
 */
public interface NetworkStub {
    /**
     * Registers [response] as the reply for all requests to [path].
     *
     * @param path Request path. Paths starting with `/3/` route to TMDB engine; others route to Trakt.
     * @param response Response body, error, or fixture to serve.
     */
    public fun stub(path: String, response: NetworkResponse)

    /**
     * Registers a dynamic reply for [path] selected from the request's query parameters.
     *
     * @param path Request path to match.
     * @param selector Function that selects a response based on query parameters.
     */
    public fun stubByQuery(path: String, selector: (Map<String, String>) -> NetworkResponse?)
}

/**
 * Reads fixture content from platform-specific sources (JVM resources, Android assets, etc.).
 */
public interface FixtureReader {
    /**
     * Returns fixture content at [path] as a string.
     *
     * @param path Resource or asset path relative to the fixtures root.
     */
    public fun read(path: String): String
}

/**
 * Registers [fixturePath] as the reply for [path].
 *
 * @param path Request path to match.
 * @param fixturePath Fixture path resolved by the registered [FixtureReader].
 */
public fun NetworkStub.stubFixture(path: String, fixturePath: String) {
    stub(path, NetworkResponse.Fixture(fixturePath))
}

/**
 * Represents a stubbed network response.
 */
public sealed interface NetworkResponse {
    /**
     * Successful response with a literal body.
     *
     * @property body Response body sent as-is.
     * @property contentType Content-Type header value. Defaults to `application/json`.
     */
    public data class Success(
        val body: String,
        val contentType: String = "application/json",
    ) : NetworkResponse

    /**
     * Error response.
     *
     * @property status HTTP status code.
     * @property body Optional error body.
     */
    public data class Error(
        val status: Int,
        val body: String = "",
    ) : NetworkResponse

    /**
     * Successful response with body loaded from a fixture file.
     *
     * @property path Fixture path resolved by the registered [FixtureReader].
     */
    public data class Fixture(
        val path: String,
    ) : NetworkResponse
}
