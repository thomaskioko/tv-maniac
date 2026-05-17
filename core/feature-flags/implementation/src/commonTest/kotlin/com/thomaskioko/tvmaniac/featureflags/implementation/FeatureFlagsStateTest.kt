package com.thomaskioko.tvmaniac.featureflags.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FeatureFlagsStateTest {

    @Test
    fun `should emit enum default given no update has occurred`() = runTest {
        val state = FeatureFlagsState()

        state.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false
        }
    }

    @Test
    fun `should emit new value after update`() = runTest {
        val state = FeatureFlagsState()

        state.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false

            state.update(mapOf(FeatureFlag.SIMKL_LOGIN_ENABLED to true))

            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should not emit duplicate values given same value written`() = runTest {
        val state = FeatureFlagsState()

        state.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false

            state.update(mapOf(FeatureFlag.SIMKL_LOGIN_ENABLED to false))

            expectNoEvents()
        }
    }

    @Test
    fun `should emit Firebase source for every flag`() = runTest {
        val state = FeatureFlagsState()

        state.source(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            awaitComplete()
        }
    }
}
