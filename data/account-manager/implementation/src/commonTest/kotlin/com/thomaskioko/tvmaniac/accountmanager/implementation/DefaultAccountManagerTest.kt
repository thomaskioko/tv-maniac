package com.thomaskioko.tvmaniac.accountmanager.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultAccountManagerTest {

    private val traktAuthRepository = FakeTraktAuthRepository()
    private val repository = DefaultAccountManager(traktAuthRepository)

    @Test
    fun `should expose TRAKT as the active provider when logged in`() = runTest {
        repository.activeProvider.test {
            awaitItem().shouldBeNull()

            traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
            awaitItem() shouldBe AccountProvider.TRAKT

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should report connected only while logged in`() = runTest {
        repository.isConnected.test {
            awaitItem() shouldBe false

            traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
            awaitItem() shouldBe true

            traktAuthRepository.setState(TraktAuthState.LOGGED_OUT)
            awaitItem() shouldBe false

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should resolve the active provider synchronously from login state`() = runTest {
        repository.getActiveProvider().shouldBeNull()

        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)

        repository.getActiveProvider() shouldBe AccountProvider.TRAKT
    }

    @Test
    fun `should emit a TRAKT connection event on sign in`() = runTest {
        repository.connectionEvents.test {
            traktAuthRepository.triggerLogin()
            awaitItem() shouldBe AccountProvider.TRAKT

            cancelAndIgnoreRemainingEvents()
        }
    }
}
