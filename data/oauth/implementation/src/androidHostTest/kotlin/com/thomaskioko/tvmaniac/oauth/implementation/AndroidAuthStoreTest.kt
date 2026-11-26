package com.thomaskioko.tvmaniac.oauth.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.time.Instant

class AndroidAuthStoreTest {

    private val folder: File = createTempDirectory("android_auth_store").toFile()
    private val authState = AuthState(
        accessToken = "access",
        refreshToken = "refresh",
        expiresAt = Instant.fromEpochMilliseconds(1_700_000_000_000),
        tokenLifetimeSeconds = 7200,
    )
    private val otherKey = stringPreferencesKey("theme")

    @Test
    fun `should write no token key to the app preferences given a saved login`() = runTest {
        val fixture = fixture()

        fixture.store.save(SyncProviderSource.TRAKT, authState)

        fixture.store.get(SyncProviderSource.TRAKT) shouldBe authState
        fixture.appPreferences.keys() shouldBe emptySet()
        fixture.deviceLocal.keys() shouldBe TRAKT_KEYS
    }

    @Test
    fun `should return and move the tokens given they are only in the app preferences`() = runTest {
        val fixture = fixture()
        fixture.appPreferences.edit {
            it[otherKey] = "dark"
            it[stringPreferencesKey("trakt_access_token")] = authState.accessToken
            it[stringPreferencesKey("trakt_refresh_token")] = authState.refreshToken
            it[longPreferencesKey("trakt_expires_at")] = authState.expiresAt!!.toEpochMilliseconds()
            it[longPreferencesKey("trakt_token_lifetime")] = authState.tokenLifetimeSeconds!!
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
    fun `should remove the tokens from both files given a clear`() = runTest {
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

    private fun TestScope.fixture(): Fixture {
        val deviceLocal = dataStore(backgroundScope, "device_local.preferences_pb")
        val appPreferences = dataStore(backgroundScope, "app.preferences_pb")
        return Fixture(
            deviceLocal = deviceLocal,
            appPreferences = appPreferences,
            store = AndroidAuthStore(
                deviceLocalDataStore = deviceLocal,
                appPreferencesDataStore = appPreferences,
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
