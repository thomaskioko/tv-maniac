package com.thomaskioko.tvmaniac.oauth.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.oauth.api.RefreshTokenResult
import com.thomaskioko.tvmaniac.oauth.testing.FakeAuthStore
import com.thomaskioko.tvmaniac.oauth.testing.FakeTokenRefreshAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultAuthStateHolderTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val authStore = FakeAuthStore()

    private fun holder() = DefaultAuthStateHolder(
        dispatchers = dispatchers,
        authStore = authStore,
        dateTimeProvider = FakeDateTimeProvider(),
        datastoreRepository = FakeDatastoreRepository(),
        requestManagerRepository = FakeRequestManagerRepository(),
        logger = FakeLogger(),
    )

    @Test
    fun `should persist tokens and report logged in given tokens saved for a provider`() =
        runTest(testDispatcher) {
            val holder = holder()

            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "trakt-access",
                refreshToken = "trakt-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )

            holder.isLoggedIn(SyncProviderSource.TRAKT) shouldBe true
            holder.getAuthState(SyncProviderSource.TRAKT)
                .shouldNotBeNull().accessToken shouldBe "trakt-access"
        }

    @Test
    fun `should emit a login event given tokens saved`() = runTest(testDispatcher) {
        val holder = holder()

        holder.loginEvents(SyncProviderSource.TRAKT).test {
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "a",
                refreshToken = "r",
                expiresAtSeconds = 1L,
            )
            awaitItem() shouldBe Unit
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should keep token state isolated given two providers are signed in`() =
        runTest(testDispatcher) {
            val holder = holder()

            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "trakt",
                refreshToken = "tr",
                expiresAtSeconds = 1L,
            )
            holder.saveTokens(
                SyncProviderSource.SIMKL,
                accessToken = "simkl",
                refreshToken = "sr",
                expiresAtSeconds = 1L,
            )

            holder.getAuthState(SyncProviderSource.TRAKT)
                .shouldNotBeNull().accessToken shouldBe "trakt"
            holder.getAuthState(SyncProviderSource.SIMKL)
                .shouldNotBeNull().accessToken shouldBe "simkl"

            holder.logout(SyncProviderSource.TRAKT)

            holder.isLoggedIn(SyncProviderSource.TRAKT) shouldBe false
            holder.getAuthState(SyncProviderSource.TRAKT).shouldBeNull()
            holder.isLoggedIn(SyncProviderSource.SIMKL) shouldBe true
            holder.getAuthState(SyncProviderSource.SIMKL)
                .shouldNotBeNull().accessToken shouldBe "simkl"
        }

    @Test
    fun `should not attempt refresh given a null action`() = runTest(testDispatcher) {
        val holder = holder()
        holder.saveTokens(
            provider = SyncProviderSource.SIMKL,
            accessToken = "s",
            refreshToken = "",
            expiresAtSeconds = 1L,
        )

        holder.refreshTokens(
            SyncProviderSource.SIMKL,
            action = null,
        ) shouldBe TokenRefreshResult.NotLoggedIn
    }

    @Test
    fun `should delegate to the action and update state given a refresh action`() =
        runTest(testDispatcher) {
            val holder = holder()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "old",
                refreshToken = "r",
                expiresAtSeconds = 1L,
            )
            val refreshed = AuthState(accessToken = "new", refreshToken = "r2", isAuthorized = true)
            val action = FakeTokenRefreshAction(RefreshTokenResult.Success(refreshed))

            val result = holder.refreshTokens(SyncProviderSource.TRAKT, action)

            result.shouldBeInstanceOf<TokenRefreshResult.Success>()
            holder.getAuthState(SyncProviderSource.TRAKT).shouldNotBeNull().accessToken shouldBe "new"
        }
}
