package com.thomaskioko.tvmaniac.featureflags.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagLocalStore
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import org.junit.After
import org.junit.Test
import kotlin.random.Random

class DebugRemoteConfigTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope: CoroutineScope = CoroutineScope(testDispatcher + Job())
    private val testFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY /
        "debug_remote_config_${Random.nextInt()}.preferences_pb"
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = testScope,
        produceFile = { testFile },
    )
    private val flagKey = "simkl_login_enabled"

    @After
    fun cleanup() = runTest {
        dataStore.edit { it.clear() }
        testScope.cancel()
        FileSystem.SYSTEM.delete(testFile)
    }

    @Test
    fun `should pass through production when debug is false`() = runTest {
        val state = RemoteConfigState().also { it.update(mapOf(flagKey to true)) }
        val production = buildProduction(state)
        val localStore = FakeFeatureFlagLocalStore().also { it.set(flagKey, false) }
        val wrapper = DebugRemoteConfig(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = false),
        )

        wrapper.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
        wrapper.observeSource(flagKey).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should let local value win over remote in debug`() = runTest {
        val state = RemoteConfigState().also { it.update(mapOf(flagKey to true)) }
        val production = buildProduction(state)
        val localStore = FakeFeatureFlagLocalStore().also { it.set(flagKey, false) }
        val wrapper = DebugRemoteConfig(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should flip source to Local when local value is set in debug`() = runTest {
        val production = buildProduction(RemoteConfigState())
        val localStore = FakeFeatureFlagLocalStore()
        val wrapper = DebugRemoteConfig(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.observeSource(flagKey).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            localStore.set(flagKey, true)
            awaitItem() shouldBe FeatureFlagSource.Local
        }
    }

    @Test
    fun `should revert to remote when local value is cleared in debug`() = runTest {
        val state = RemoteConfigState().also { it.update(mapOf(flagKey to true)) }
        val production = buildProduction(state)
        val localStore = FakeFeatureFlagLocalStore().also { it.set(flagKey, false) }
        val wrapper = DebugRemoteConfig(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe false
            localStore.clear(flagKey)
            awaitItem() shouldBe true
        }
    }

    private fun buildProduction(state: RemoteConfigState): AndroidRemoteConfig =
        AndroidRemoteConfig(
            remoteConfig = null,
            settings = FirebaseRemoteConfigSettings.Builder().build(),
            state = state,
            flags = lazyOf(emptySet()),
            logger = FakeLogger(),
            scope = testScope,
        )

    private class StubDebugConfig(override val isDebug: Boolean) : DebugConfig
}
