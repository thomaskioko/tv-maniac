package com.thomaskioko.tvmaniac.data.ratings.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRemoteDataSource
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class ActiveRatingsRemoteDataSourceBindingContainerTest {

    @Test
    fun `should select source matching the active provider given multiple sources are registered`() {
        val traktSource = FakeRatingsRemoteDataSource().apply { provider = AccountProvider.TRAKT }
        val simklSource = FakeRatingsRemoteDataSource().apply { provider = AccountProvider.SIMKL }
        val accountManager = FakeAccountManager().apply { setActiveProvider(AccountProvider.SIMKL) }

        val result = ActiveRatingsRemoteDataSourceBindingContainer.activeRatingsRemoteDataSource(
            sources = setOf(traktSource, simklSource),
            accountManager = accountManager,
        )

        result shouldBe simklSource
    }

    @Test
    fun `should return null given no active provider is set`() {
        val traktSource = FakeRatingsRemoteDataSource().apply { provider = AccountProvider.TRAKT }
        val accountManager = FakeAccountManager()

        val result = ActiveRatingsRemoteDataSourceBindingContainer.activeRatingsRemoteDataSource(
            sources = setOf(traktSource),
            accountManager = accountManager,
        )

        result shouldBe null
    }
}
