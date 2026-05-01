package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import io.kotest.assertions.throwables.shouldNotThrowAny
import org.junit.Test

/**
 * Sanity-check every entry in the endpoint catalog: both `successFixture` and `errorFixture`
 * must resolve to a readable resource on the classpath. Catches typos in the catalog or missing
 * `error.json` files at unit-test time, before they'd surface as opaque "no stub registered"
 * errors during a full integration test.
 */
class EndpointsCatalogTest {

    @Test
    fun `every endpoint's success fixture exists on the classpath`() {
        Endpoints.all.forEach { endpoint ->
            shouldNotThrowAny(label(endpoint, endpoint.successFixture)) {
                FixtureLoader.load(endpoint.successFixture)
            }
        }
    }

    @Test
    fun `every endpoint's error fixture exists on the classpath`() {
        Endpoints.all.forEach { endpoint ->
            shouldNotThrowAny(label(endpoint, endpoint.errorFixture)) {
                FixtureLoader.load(endpoint.errorFixture)
            }
        }
    }

    private fun label(endpoint: Endpoint, fixturePath: String): String {
        val matcher = when (endpoint) {
            is Endpoint.Exact -> endpoint.path
            is Endpoint.Pattern -> endpoint.pathRegex
        }
        return "$matcher -> $fixturePath"
    }
}

private inline fun shouldNotThrowAny(label: String, block: () -> Unit) {
    try {
        block()
    } catch (t: Throwable) {
        throw AssertionError("Endpoint check failed for $label: ${t.message}", t)
    }
}
