package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.testing.integration.bindings.TestTmdbBindingContainer
import com.thomaskioko.tvmaniac.testing.integration.bindings.TestTraktBindingContainer
import org.junit.rules.ExternalResource

public class MockEngineResetRule : ExternalResource() {
    override fun before() {
        TestTmdbBindingContainer.handler.reset()
        TestTraktBindingContainer.handler.reset()
    }
}
