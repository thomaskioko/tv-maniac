package com.thomaskioko.tvmaniac.oauth.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.time.Instant

internal class AndroidAuthStoreTest {

    private val folder: File = createTempDirectory("android_auth_store").toFile()
    private val authState = AuthState(
        accessToken = "access",
        refreshToken = "refresh",
        expiresAt = Instant.fromEpochMilliseconds(1_700_000_000_000),
        tokenLifetimeSeconds = 7200,
    )
    private val newAuthState = AuthState(
        accessToken = "new-access",
        refreshToken = "new-refresh",
        expiresAt = Instant.fromEpochMilliseconds(1_800_000_000_000),
        tokenLifetimeSeconds = 3600,
    )
    private val otherKey = stringPreferencesKey("theme")

    @Test
    fun `should write the tokens only to the device local file given a saved login`() = runTest {
        val fixture = fixture()
        fixture.appPreferences.edit {
            it[otherKey] = "dark"
            it.putTokens(prefix = "trakt", state = authState)
        }

        fixture.store.save(SyncProviderSource.TRAKT, newAuthState)

        fixture.appPreferences.keys() shouldBe setOf("theme")
        fixture.appPreferences.data.first()[otherKey] shouldBe "dark"
        fixture.deviceLocal.keys() shouldBe TRAKT_KEYS
        fixture.deviceLocal.data.first().toTokens(prefix = "trakt") shouldBe newAuthState
    }

    @Test
    fun `should return and move the tokens given they are only in the app preferences`() = runTest {
        val fixture = fixture()
        fixture.appPreferences.edit {
            it[otherKey] = "dark"
            it.putTokens(prefix = "trakt", state = authState)
        }

        fixture.store.get(SyncProviderSource.TRAKT) shouldBe authState

        fixture.deviceLocal.keys() shouldBe TRAKT_KEYS
        fixture.appPreferences.keys() shouldBe setOf("theme")
        fixture.appPreferences.data.first()[otherKey] shouldBe "dark"
        fixture.store.get(SyncProviderSource.TRAKT) shouldBe authState
    }

    @Test
    fun `should prefer the device local tokens given both files hold tokens`() = runTest {
        val fixture = fixture()
        fixture.store.save(SyncProviderSource.TRAKT, authState)
        fixture.appPreferences.edit {
            it[stringPreferencesKey("trakt_access_token")] = "old-access"
            it[stringPreferencesKey("trakt_refresh_token")] = "old-refresh"
        }

        fixture.store.get(SyncProviderSource.TRAKT) shouldBe authState
    }

    @Test
    fun `should remove the provider's tokens from both files given a logout`() = runTest {
        val fixture = fixture()
        fixture.store.save(SyncProviderSource.TRAKT, authState)
        fixture.store.save(SyncProviderSource.SIMKL, authState)
        fixture.appPreferences.edit {
            it[otherKey] = "dark"
            it[stringPreferencesKey("trakt_access_token")] = "old-access"
            it[stringPreferencesKey("trakt_refresh_token")] = "old-refresh"
        }

        fixture.store.clear(SyncProviderSource.TRAKT)

        fixture.store.get(SyncProviderSource.TRAKT) shouldBe null
        fixture.deviceLocal.keys() shouldBe SIMKL_KEYS
        fixture.appPreferences.keys() shouldBe setOf("theme")
    }

    @Test
    fun `should stay logged out given a logout while the first read is moving the old tokens`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val fixture = fixture(gate = gate)
        fixture.appPreferences.edit { it.putTokens(prefix = "trakt", state = authState) }

        val firstRead = async { fixture.store.get(SyncProviderSource.TRAKT) }
        runCurrent()
        val logout = async { fixture.store.clear(SyncProviderSource.TRAKT) }
        runCurrent()
        gate.complete(Unit)
        awaitAll(firstRead, logout)

        fixture.store.get(SyncProviderSource.TRAKT) shouldBe null
        fixture.deviceLocal.keys() shouldBe emptySet()
        fixture.appPreferences.keys() shouldBe emptySet()
    }

    @Test
    fun `should keep the new login given a login while the first read is moving the old tokens`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val fixture = fixture(gate = gate)
        fixture.appPreferences.edit {
            it[otherKey] = "dark"
            it.putTokens(prefix = "trakt", state = authState)
        }

        val firstRead = async { fixture.store.get(SyncProviderSource.TRAKT) }
        runCurrent()
        val login = async { fixture.store.save(SyncProviderSource.TRAKT, newAuthState) }
        runCurrent()
        gate.complete(Unit)
        awaitAll(firstRead, login)

        fixture.deviceLocal.data.first().toTokens(prefix = "trakt") shouldBe newAuthState
        fixture.deviceLocal.keys() shouldBe TRAKT_KEYS
        fixture.appPreferences.keys() shouldBe setOf("theme")
        fixture.appPreferences.data.first()[otherKey] shouldBe "dark"
    }

    private fun TestScope.fixture(gate: CompletableDeferred<Unit>? = null): Fixture {
        val deviceLocal = dataStore(backgroundScope, "device_local.preferences_pb")
        val appPreferences = dataStore(backgroundScope, "app.preferences_pb")
        return Fixture(
            deviceLocal = deviceLocal,
            appPreferences = appPreferences,
            store = AndroidAuthStore(
                deviceLocalDataStore = deviceLocal,
                appPreferencesDataStore = gate?.let { GatedDataStore(appPreferences, it) } ?: appPreferences,
            ),
        )
    }

    private fun dataStore(scope: CoroutineScope, name: String): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope,
            produceFile = { File(folder, name).absolutePath.toPath() },
        )

    private suspend fun DataStore<Preferences>.keys(): Set<String> = data.first().asMap().keys.map { it.name }.toSet()

    private fun MutablePreferences.putTokens(prefix: String, state: AuthState) {
        this[stringPreferencesKey("${prefix}_access_token")] = state.accessToken
        this[stringPreferencesKey("${prefix}_refresh_token")] = state.refreshToken
        this[longPreferencesKey("${prefix}_expires_at")] = state.expiresAt!!.toEpochMilliseconds()
        this[longPreferencesKey("${prefix}_token_lifetime")] = state.tokenLifetimeSeconds!!
    }

    private fun Preferences.toTokens(prefix: String): AuthState = AuthState(
        accessToken = this[stringPreferencesKey("${prefix}_access_token")]!!,
        refreshToken = this[stringPreferencesKey("${prefix}_refresh_token")]!!,
        expiresAt = this[longPreferencesKey("${prefix}_expires_at")]?.let { Instant.fromEpochMilliseconds(it) },
        tokenLifetimeSeconds = this[longPreferencesKey("${prefix}_token_lifetime")],
    )

    private class GatedDataStore(
        private val delegate: DataStore<Preferences>,
        private val gate: CompletableDeferred<Unit>,
    ) : DataStore<Preferences> {
        override val data: Flow<Preferences> = flow {
            val snapshot = delegate.data.first()
            gate.await()
            emit(snapshot)
        }

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
            delegate.updateData(transform)
    }

    private class Fixture(
        val deviceLocal: DataStore<Preferences>,
        val appPreferences: DataStore<Preferences>,
        val store: AndroidAuthStore,
    )

    private companion object {
        val TRAKT_KEYS = setOf("trakt_access_token", "trakt_refresh_token", "trakt_expires_at", "trakt_token_lifetime")
        val SIMKL_KEYS = setOf("simkl_access_token", "simkl_refresh_token", "simkl_expires_at", "simkl_token_lifetime")
    }
}
