package com.thomaskioko.tvmaniac.testing.integration

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import platform.Foundation.NSProcessInfo

public const val STUB_SCENARIO_ENV: String = "TVMANIAC_STUB_SCENARIO"
public const val CLEAR_STATE_ENV: String = "TVMANIAC_CLEAR_STATE"

/** XCUITest runs in a separate process, so the launch environment is its only channel. */
public object StubHttpEngine {

    private val environment: Map<Any?, *> get() = NSProcessInfo.processInfo.environment

    public val shouldClearPersistentState: Boolean
        get() = environment[CLEAR_STATE_ENV] as String? == "1"

    public val scenario: Scenario? by lazy {
        (environment[STUB_SCENARIO_ENV] as String?)?.let { Scenarios.apply(MockEngineHandler.handler, it) }
    }

    /** Returns null when no scenario was named, which is every launch outside a UI test. */
    public fun createOrNull(): HttpClientEngine? {
        scenario ?: return null
        return MockEngine { request -> MockEngineHandler.handler.handle(this, request) }
    }
}
