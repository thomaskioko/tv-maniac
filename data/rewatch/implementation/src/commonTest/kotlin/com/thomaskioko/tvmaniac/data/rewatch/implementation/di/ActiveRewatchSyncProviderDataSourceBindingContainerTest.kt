package com.thomaskioko.tvmaniac.data.rewatch.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchSyncProviderDataSource
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class ActiveRewatchSyncProviderDataSourceBindingContainerTest {

    @Test
    fun `should select source matching the active provider given multiple sources are registered`() {
        val traktSource = FakeRewatchSyncProviderDataSource(provider = SyncProviderSource.TRAKT)
        val simklSource = FakeRewatchSyncProviderDataSource(provider = SyncProviderSource.SIMKL)
        val accountManager = FakeAccountManager().apply { setActiveProvider(SyncProviderSource.SIMKL) }

        val result = ActiveRewatchSyncProviderDataSourceBindingContainer.activeRewatchSyncProviderDataSource(
            sources = setOf(traktSource, simklSource),
            accountManager = accountManager,
        )

        result shouldBe simklSource
    }

    @Test
    fun `should return null given no active provider is set`() {
        val traktSource = FakeRewatchSyncProviderDataSource(provider = SyncProviderSource.TRAKT)
        val accountManager = FakeAccountManager()

        val result = ActiveRewatchSyncProviderDataSourceBindingContainer.activeRewatchSyncProviderDataSource(
            sources = setOf(traktSource),
            accountManager = accountManager,
        )

        result shouldBe null
    }
}
