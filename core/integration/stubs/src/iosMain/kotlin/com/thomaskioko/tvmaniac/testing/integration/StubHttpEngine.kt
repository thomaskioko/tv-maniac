package com.thomaskioko.tvmaniac.testing.integration

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import platform.Foundation.NSProcessInfo

public const val STUB_SCENARIO_ENV: String = "TVMANIAC_STUB_SCENARIO"

public object StubHttpEngine {

    private val environment: Map<Any?, *> get() = NSProcessInfo.processInfo.environment

    private val scenarioName: String? by lazy {
        (environment[STUB_SCENARIO_ENV] as String?)?.also { Scenarios.apply(MockEngineHandler.handler, it) }
    }

    public fun createOrNull(): HttpClientEngine? {
        scenarioName ?: return null
        return MockEngine { request -> MockEngineHandler.handler.handle(this, request) }
    }
}
