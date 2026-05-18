package com.thomaskioko.tvmaniac.featureflags.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import com.thomaskioko.tvmaniac.featureflags.observe
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagLocalStore
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultFeatureFlagProviderTest {

    private val localStore = FakeFeatureFlagLocalStore()
    private val provider = DefaultFeatureFlagProvider(localStore = localStore)

    @Test
    fun `should order flags by title ascending`() {
        val sorted = provider.flags(FeatureFlagSortDescriptor.Title, ascending = true)
        sorted shouldBe FeatureFlag.entries.sortedBy { it.title }
    }

    @Test
    fun `should order flags by title descending`() {
        val sorted = provider.flags(FeatureFlagSortDescriptor.Title, ascending = false)
        sorted shouldBe FeatureFlag.entries.sortedByDescending { it.title }
    }

    @Test
    fun `should order flags by key ascending`() {
        val sorted = provider.flags(FeatureFlagSortDescriptor.Key, ascending = true)
        sorted shouldBe FeatureFlag.entries.sortedBy { it.key }
    }

    @Test
    fun `should order flags by date descending`() {
        val sorted = provider.flags(FeatureFlagSortDescriptor.Date, ascending = false)
        sorted shouldBe FeatureFlag.entries.sortedByDescending { it.dateAdded }
    }

    @Test
    fun `should find flag by key`() {
        val found = provider.findFeatureFlag(FeatureFlag.SIMKL_LOGIN_ENABLED.key)
        found shouldBe FeatureFlag.SIMKL_LOGIN_ENABLED
    }

    @Test
    fun `should return null when key is unknown`() {
        val found = provider.findFeatureFlag("unknown_flag")
        found shouldBe null
    }

    @Test
    fun `should clear all local values via resetAllLocals`() = runTest {
        localStore.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)

        provider.resetAllLocals()

        localStore.observe<Boolean>(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }
}
