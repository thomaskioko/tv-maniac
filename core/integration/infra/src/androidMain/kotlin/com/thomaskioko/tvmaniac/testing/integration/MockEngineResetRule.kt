package com.thomaskioko.tvmaniac.testing.integration

import org.junit.rules.ExternalResource

public class MockEngineResetRule : ExternalResource() {
    override fun before() {
        MockEngineHandler.handler.reset()
    }
}
