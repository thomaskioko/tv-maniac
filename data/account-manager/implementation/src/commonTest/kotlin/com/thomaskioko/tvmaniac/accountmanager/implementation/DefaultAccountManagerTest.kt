package com.thomaskioko.tvmaniac.accountmanager.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountAuthRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultAccountManagerTest {

    private val traktRepository = FakeAccountAuthRepository(AccountProvider.TRAKT)
    private val accountManager = DefaultAccountManager(authRepositories = setOf(traktRepository))

    @Test
    fun `should expose TRAKT as the active provider when logged in`() = runTest {
        accountManager.activeProvider.test {
            awaitItem().shouldBeNull()

            traktRepository.setState(AccountAuthState.LOGGED_IN)
            awaitItem() shouldBe AccountProvider.TRAKT

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should report connected only while logged in`() = runTest {
        accountManager.isConnected.test {
            awaitItem() shouldBe false

            traktRepository.setState(AccountAuthState.LOGGED_IN)
            awaitItem() shouldBe true

            traktRepository.setState(AccountAuthState.LOGGED_OUT)
            awaitItem() shouldBe false

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should resolve the active provider synchronously from login state`() = runTest {
        accountManager.getActiveProvider().shouldBeNull()

        traktRepository.setState(AccountAuthState.LOGGED_IN)

        accountManager.getActiveProvider() shouldBe AccountProvider.TRAKT
    }

    @Test
    fun `should emit a TRAKT connection event on sign in`() = runTest {
        accountManager.connectionEvents.test {
            traktRepository.triggerLogin()
            awaitItem() shouldBe AccountProvider.TRAKT

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear the active provider after logging out`() = runTest {
        traktRepository.setState(AccountAuthState.LOGGED_IN)
        accountManager.getActiveProvider() shouldBe AccountProvider.TRAKT

        accountManager.logout(AccountProvider.TRAKT)

        accountManager.getActiveProvider().shouldBeNull()
    }

    @Test
    fun `should refresh tokens for the active provider`() = runTest {
        traktRepository.setState(AccountAuthState.LOGGED_IN)
        traktRepository.setRefreshOutcome(TokenRefreshResult.TokenRevoked)

        accountManager.refreshActiveTokens() shouldBe TokenRefreshResult.TokenRevoked
    }

    @Test
    fun `should return NotLoggedIn when refreshing without an active provider`() = runTest {
        accountManager.refreshActiveTokens() shouldBe TokenRefreshResult.NotLoggedIn
    }

    @Test
    fun `should expose the active account when logged in`() = runTest {
        accountManager.activeAccount.test {
            awaitItem().shouldBeNull()

            traktRepository.setState(AccountAuthState.LOGGED_IN)
            val account = awaitItem()
            account?.provider shouldBe AccountProvider.TRAKT
            account?.isConnected shouldBe true
            account?.isActive shouldBe true

            cancelAndIgnoreRemainingEvents()
        }
    }
}
