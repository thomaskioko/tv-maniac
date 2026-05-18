package com.thomaskioko.tvmaniac.featureflags.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.observe
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test

internal class DefaultFeatureFlagLocalStoreTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope: CoroutineScope = CoroutineScope(testDispatcher + Job())
    private val testFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY /
        "feature_flag_locals_${Random.nextInt()}.preferences_pb"

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = testScope,
        produceFile = { testFile },
    )

    private val store = DefaultFeatureFlagLocalStore(dataStore = dataStore)

    @AfterTest
    fun cleanup() = runTest {
        dataStore.edit { it.clear() }
        testScope.cancel()
        FileSystem.SYSTEM.delete(testFile)
    }

    @Test
    fun `should emit null given key has no local value`() = runTest {
        store.observe<Boolean>(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit Boolean value after setLocal`() = runTest {
        store.observe<Boolean>(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe null
            store.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)
            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should emit Int value after setLocal`() = runTest {
        store.observe<Int>(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe null
            store.set(FeatureFlag.SIMKL_LOGIN_ENABLED, 42)
            awaitItem() shouldBe 42
        }
    }

    @Test
    fun `should emit String value after setLocal`() = runTest {
        store.observe<String>(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe null
            store.set(FeatureFlag.SIMKL_LOGIN_ENABLED, "hello")
            awaitItem() shouldBe "hello"
        }
    }

    @Test
    fun `should emit null again after clearLocal`() = runTest {
        store.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)

        store.observe<Boolean>(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe true
            store.clear(FeatureFlag.SIMKL_LOGIN_ENABLED)
            awaitItem() shouldBe null
        }
    }

    @Test
    fun `should emit empty map after clearAllLocals`() = runTest {
        store.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)

        store.observeAll().test {
            awaitItem() shouldBe mapOf<FeatureFlag, Any>(FeatureFlag.SIMKL_LOGIN_ENABLED to true)
            store.clearAll()
            awaitItem() shouldBe emptyMap()
        }
    }

    @Test
    fun `should persist value across re-construction with same DataStore`() = runTest {
        store.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)

        val secondStore = DefaultFeatureFlagLocalStore(dataStore = dataStore)
        secondStore.observe<Boolean>(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }
}
