package com.thomaskioko.tvmaniac.featureflags.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
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

class AndroidDebugFeatureFlagsTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope: CoroutineScope = CoroutineScope(testDispatcher + Job())
    private val testFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY /
        "android_debug_feature_flags_${Random.nextInt()}.preferences_pb"
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = testScope,
        produceFile = { testFile },
    )

    @After
    fun cleanup() = runTest {
        dataStore.edit { it.clear() }
        testScope.cancel()
        FileSystem.SYSTEM.delete(testFile)
    }

    @Test
    fun `should pass through production when debug is false`() = runTest {
        val state = FeatureFlagsState().also {
            it.update(mapOf(FeatureFlag.SIMKL_LOGIN_ENABLED to true))
        }
        val production = buildProduction(state)
        val localStore = FakeFeatureFlagLocalStore().also {
            it.set(FeatureFlag.SIMKL_LOGIN_ENABLED, false)
        }
        val wrapper = AndroidDebugFeatureFlags(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = false),
        )

        wrapper.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
        wrapper.source(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should let local value win over remote in debug`() = runTest {
        val state = FeatureFlagsState().also {
            it.update(mapOf(FeatureFlag.SIMKL_LOGIN_ENABLED to true))
        }
        val production = buildProduction(state)
        val localStore = FakeFeatureFlagLocalStore().also {
            it.set(FeatureFlag.SIMKL_LOGIN_ENABLED, false)
        }
        val wrapper = AndroidDebugFeatureFlags(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should flip source to Local when local value is set in debug`() = runTest {
        val production = buildProduction(FeatureFlagsState())
        val localStore = FakeFeatureFlagLocalStore()
        val wrapper = AndroidDebugFeatureFlags(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.source(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            localStore.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)
            awaitItem() shouldBe FeatureFlagSource.Local
        }
    }

    @Test
    fun `should revert to remote when local value is cleared in debug`() = runTest {
        val state = FeatureFlagsState().also {
            it.update(mapOf(FeatureFlag.SIMKL_LOGIN_ENABLED to true))
        }
        val production = buildProduction(state)
        val localStore = FakeFeatureFlagLocalStore().also {
            it.set(FeatureFlag.SIMKL_LOGIN_ENABLED, false)
        }
        val wrapper = AndroidDebugFeatureFlags(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false
            localStore.clear(FeatureFlag.SIMKL_LOGIN_ENABLED)
            awaitItem() shouldBe true
        }
    }

    private fun buildProduction(state: FeatureFlagsState): AndroidRemoteConfigFeatureFlags =
        AndroidRemoteConfigFeatureFlags(
            remoteConfig = null,
            settings = FirebaseRemoteConfigSettings.Builder().build(),
            state = state,
            logger = FakeLogger(),
            scope = testScope,
        )

    private class StubDebugConfig(override val isDebug: Boolean) : DebugConfig
}
