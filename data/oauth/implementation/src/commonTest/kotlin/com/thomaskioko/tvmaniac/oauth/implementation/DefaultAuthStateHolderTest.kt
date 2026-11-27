package com.thomaskioko.tvmaniac.oauth.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.oauth.api.AuthStore
import com.thomaskioko.tvmaniac.oauth.api.RefreshTokenResult
import com.thomaskioko.tvmaniac.oauth.api.TokenRefreshAction
import com.thomaskioko.tvmaniac.oauth.testing.FakeAuthStore
import com.thomaskioko.tvmaniac.oauth.testing.FakeTokenRefreshAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

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
    private val dateTimeProvider = FakeDateTimeProvider()
    private val datastoreRepository = FakeDatastoreRepository()

    private fun holder(store: AuthStore = authStore) = DefaultAuthStateHolder(
        dispatchers = dispatchers,
        authStore = store,
        dateTimeProvider = dateTimeProvider,
        datastoreRepository = datastoreRepository,
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

    @Test
    fun `should stay signed out given a sign-out during the start-up read`() = runTest(testDispatcher) {
        authStore.save(SyncProviderSource.TRAKT, signedIn("stale"))
        val gatedStore = GatedAuthStore(authStore)
        gatedStore.hold(SyncProviderSource.TRAKT)
        val holder = holder(gatedStore)
        runCurrent()

        val signOut = launch { holder.logout(SyncProviderSource.TRAKT) }
        runCurrent()
        gatedStore.release()
        advanceUntilIdle()

        signOut.isCompleted shouldBe true
        holder.isLoggedIn(SyncProviderSource.TRAKT) shouldBe false
        holder.getAuthState(SyncProviderSource.TRAKT).shouldBeNull()
    }

    @Test
    fun `should keep the new login given a sign-in during the start-up read`() = runTest(testDispatcher) {
        authStore.save(SyncProviderSource.TRAKT, signedIn("stale"))
        val gatedStore = GatedAuthStore(authStore)
        gatedStore.hold(SyncProviderSource.TRAKT)
        val holder = holder(gatedStore)
        runCurrent()

        val signIn = launch {
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "fresh",
                refreshToken = "fresh-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
        }
        runCurrent()
        gatedStore.release()
        advanceUntilIdle()

        signIn.isCompleted shouldBe true
        holder.getAuthState(SyncProviderSource.TRAKT).shouldNotBeNull().accessToken shouldBe "fresh"
    }

    @Test
    fun `should stay signed out given a sign-out during a store read after the cached login expired`() =
        runTest(testDispatcher) {
            authStore.save(SyncProviderSource.TRAKT, signedIn("stale"))
            val gatedStore = GatedAuthStore(authStore)
            val holder = holder(gatedStore)
            advanceUntilIdle()
            dateTimeProvider.setCurrentTime(dateTimeProvider.now() + 2.hours)
            gatedStore.hold(SyncProviderSource.TRAKT)

            val read = launch { holder.getAuthState(SyncProviderSource.TRAKT) }
            runCurrent()
            val signOut = launch { holder.logout(SyncProviderSource.TRAKT) }
            runCurrent()
            gatedStore.release()
            advanceUntilIdle()

            read.isCompleted shouldBe true
            signOut.isCompleted shouldBe true
            holder.isLoggedIn(SyncProviderSource.TRAKT) shouldBe false
        }

    @Test
    fun `should stay signed out given a sign-out during a token refresh`() = runTest(testDispatcher) {
        val holder = holder()
        advanceUntilIdle()
        holder.saveTokens(
            provider = SyncProviderSource.TRAKT,
            accessToken = "old",
            refreshToken = "old-refresh",
            expiresAtSeconds = 9_999_999_999L,
        )
        holder.setAuthError(SyncProviderSource.TRAKT, AuthError.NetworkError)
        datastoreRepository.setLastTokenRefreshTimestamp(PREVIOUS_REFRESH_MILLIS)
        val action = GatedTokenRefreshAction(RefreshTokenResult.Success(signedIn("refreshed")))

        val refresh = async { holder.refreshTokens(SyncProviderSource.TRAKT, action) }
        runCurrent()
        holder.logout(SyncProviderSource.TRAKT)
        action.release()
        advanceUntilIdle()

        refresh.await() shouldBe TokenRefreshResult.NotLoggedIn
        holder.isLoggedIn(SyncProviderSource.TRAKT) shouldBe false
        holder.getAuthState(SyncProviderSource.TRAKT).shouldBeNull()
        holder.authError(SyncProviderSource.TRAKT).first() shouldBe AuthError.NetworkError
        datastoreRepository.observeLastTokenRefreshTimestamp().first() shouldBe PREVIOUS_REFRESH_MILLIS
    }

    @Test
    fun `should keep the new login given a sign-in during a token refresh that succeeds`() =
        runTest(testDispatcher) {
            val holder = holder()
            advanceUntilIdle()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "old",
                refreshToken = "old-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            val action = GatedTokenRefreshAction(RefreshTokenResult.Success(signedIn("refreshed")))

            val refresh = async { holder.refreshTokens(SyncProviderSource.TRAKT, action) }
            runCurrent()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "fresh",
                refreshToken = "fresh-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            action.release()
            advanceUntilIdle()

            refresh.await() shouldBe TokenRefreshResult.Success(freshLogin())
            holder.getAuthState(SyncProviderSource.TRAKT) shouldBe freshLogin()
        }

    @Test
    fun `should keep the new login given a sign-in during a token refresh that fails with a network error`() =
        runTest(testDispatcher) {
            val holder = holder()
            advanceUntilIdle()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "old",
                refreshToken = "old-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            val action = GatedTokenRefreshAction(RefreshTokenResult.NetworkError("offline"))

            val refresh = async { holder.refreshTokens(SyncProviderSource.TRAKT, action) }
            runCurrent()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "fresh",
                refreshToken = "fresh-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            action.release()
            advanceUntilIdle()

            refresh.await() shouldBe TokenRefreshResult.Success(freshLogin())
            holder.getAuthState(SyncProviderSource.TRAKT) shouldBe freshLogin()
            holder.authError(SyncProviderSource.TRAKT).first().shouldBeNull()
        }

    @Test
    fun `should keep the new login and the auth error given a sign-in during a token refresh that fails`() =
        runTest(testDispatcher) {
            val holder = holder()
            advanceUntilIdle()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "old",
                refreshToken = "old-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            holder.setAuthError(SyncProviderSource.TRAKT, AuthError.NetworkError)
            val action = GatedTokenRefreshAction(RefreshTokenResult.Failed("server error"))

            val refresh = async { holder.refreshTokens(SyncProviderSource.TRAKT, action) }
            runCurrent()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "fresh",
                refreshToken = "fresh-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            action.release()
            advanceUntilIdle()

            refresh.await() shouldBe TokenRefreshResult.Success(freshLogin())
            holder.getAuthState(SyncProviderSource.TRAKT) shouldBe freshLogin()
            holder.authError(SyncProviderSource.TRAKT).first() shouldBe AuthError.NetworkError
        }

    @Test
    fun `should apply the refreshed tokens given the same login reloaded with another expiry during a token refresh`() =
        runTest(testDispatcher) {
            val holder = holder()
            advanceUntilIdle()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "old",
                refreshToken = "old-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            authStore.save(
                SyncProviderSource.TRAKT,
                AuthState(
                    accessToken = "old",
                    refreshToken = "old-refresh",
                    isAuthorized = true,
                    expiresAt = Instant.fromEpochMilliseconds(9_999_999_999_123L),
                ),
            )
            val refreshed = signedIn("refreshed")
            val action = GatedTokenRefreshAction(RefreshTokenResult.Success(refreshed))

            val refresh = async { holder.refreshTokens(SyncProviderSource.TRAKT, action) }
            runCurrent()
            dateTimeProvider.setCurrentTime(dateTimeProvider.now() + 2.hours)
            holder.getAuthState(SyncProviderSource.TRAKT)
                .shouldNotBeNull().expiresAt shouldBe Instant.fromEpochMilliseconds(9_999_999_999_123L)
            action.release()
            advanceUntilIdle()

            refresh.await() shouldBe TokenRefreshResult.Success(refreshed)
            holder.getAuthState(SyncProviderSource.TRAKT) shouldBe refreshed
        }

    @Test
    fun `should keep the new login given a sign-in during a token refresh that finds the token expired`() =
        runTest(testDispatcher) {
            val holder = holder()
            advanceUntilIdle()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "old",
                refreshToken = "old-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            val action = GatedTokenRefreshAction(RefreshTokenResult.TokenExpired)

            val refresh = async { holder.refreshTokens(SyncProviderSource.TRAKT, action) }
            runCurrent()
            holder.saveTokens(
                provider = SyncProviderSource.TRAKT,
                accessToken = "fresh",
                refreshToken = "fresh-refresh",
                expiresAtSeconds = 9_999_999_999L,
            )
            action.release()
            advanceUntilIdle()

            refresh.await() shouldBe TokenRefreshResult.Success(freshLogin())
            holder.getAuthState(SyncProviderSource.TRAKT) shouldBe freshLogin()
            holder.authError(SyncProviderSource.TRAKT).first().shouldBeNull()
        }

    private fun signedIn(accessToken: String) = AuthState(
        accessToken = accessToken,
        refreshToken = "refresh",
        isAuthorized = true,
    )

    private fun freshLogin() = AuthState(
        accessToken = "fresh",
        refreshToken = "fresh-refresh",
        isAuthorized = true,
        expiresAt = Instant.fromEpochSeconds(9_999_999_999L),
    )

    private companion object {
        private const val PREVIOUS_REFRESH_MILLIS = 1_000L
    }
}

private class GatedAuthStore(private val delegate: FakeAuthStore) : AuthStore {
    private var gatedProvider: SyncProviderSource? = null
    private var gate = CompletableDeferred<Unit>()

    fun hold(provider: SyncProviderSource) {
        gatedProvider = provider
        gate = CompletableDeferred()
    }

    fun release() {
        gate.complete(Unit)
    }

    override suspend fun get(provider: SyncProviderSource): AuthState? {
        val stored = delegate.get(provider)
        if (provider == gatedProvider) gate.await()
        return stored
    }

    override suspend fun save(provider: SyncProviderSource, state: AuthState) {
        delegate.save(provider, state)
    }

    override suspend fun clear(provider: SyncProviderSource) {
        delegate.clear(provider)
    }
}

private class GatedTokenRefreshAction(private val result: RefreshTokenResult) : TokenRefreshAction {
    private val gate = CompletableDeferred<Unit>()

    fun release() {
        gate.complete(Unit)
    }

    override suspend fun invoke(currentState: AuthState): RefreshTokenResult {
        gate.await()
        return result
    }
}
