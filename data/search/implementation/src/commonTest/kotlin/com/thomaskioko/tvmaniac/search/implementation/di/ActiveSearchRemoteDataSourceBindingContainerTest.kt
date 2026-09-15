package com.thomaskioko.tvmaniac.search.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRemoteDataSource
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class ActiveSearchRemoteDataSourceBindingContainerTest {

    private val traktSource = FakeSearchRemoteDataSource(provider = SyncProviderSource.TRAKT)
    private val simklSource = FakeSearchRemoteDataSource(provider = SyncProviderSource.SIMKL)
    private val sources = setOf(traktSource, simklSource)

    @Test
    fun `should select trakt source given no provider is active`() {
        val accountManager = FakeAccountManager()

        val activeSource = ActiveSearchRemoteDataSourceBindingContainer.activeSearchRemoteDataSource(
            sources = sources,
            accountManager = accountManager,
        )

        activeSource shouldBe traktSource
    }

    @Test
    fun `should select simkl source given simkl is the active provider`() {
        val accountManager = FakeAccountManager()
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)

        val activeSource = ActiveSearchRemoteDataSourceBindingContainer.activeSearchRemoteDataSource(
            sources = sources,
            accountManager = accountManager,
        )

        activeSource shouldBe simklSource
    }
}
