package com.thomaskioko.tvmaniac.accountmanager.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.NoProviderFeatures
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.implementation.di.ActiveProviderFeaturesBindingContainer
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ActiveProviderFeaturesProviderTest {

    private val traktFeatures = FakeProviderFeatures(
        supportsContinueWatchingFetch = true,
        supportsFavorites = true,
        supportsLists = true,
        supportsCalendar = true,
    )
    private val simklFeatures = FakeProviderFeatures(
        supportsContinueWatchingFetch = false,
        supportsFavorites = false,
        supportsLists = false,
        supportsCalendar = false,
    )
    private val featuresMap = mapOf(
        SyncProviderSource.TRAKT to traktFeatures,
        SyncProviderSource.SIMKL to simklFeatures,
    )
    private val accountManager = FakeAccountManager()

    @Test
    fun `should return trakt features given trakt is the active provider`() {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        val result = ActiveProviderFeaturesBindingContainer.activeProviderFeatures(
            features = featuresMap,
            accountManager = accountManager,
        )

        result.supportsContinueWatchingFetch shouldBe true
        result.supportsFavorites shouldBe true
        result.supportsLists shouldBe true
        result.supportsCalendar shouldBe true
    }

    @Test
    fun `should return simkl features given simkl is the active provider`() {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)

        val result = ActiveProviderFeaturesBindingContainer.activeProviderFeatures(
            features = featuresMap,
            accountManager = accountManager,
        )

        result.supportsContinueWatchingFetch shouldBe false
        result.supportsFavorites shouldBe false
        result.supportsLists shouldBe false
        result.supportsCalendar shouldBe false
    }

    @Test
    fun `should return NoProviderFeatures given no active provider`() {
        accountManager.setActiveProvider(null)

        val result = ActiveProviderFeaturesBindingContainer.activeProviderFeatures(
            features = featuresMap,
            accountManager = accountManager,
        )

        result shouldBe NoProviderFeatures
    }

    @Test
    fun `should return NoProviderFeatures given active provider not in features map`() {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        val result = ActiveProviderFeaturesBindingContainer.activeProviderFeatures(
            features = emptyMap(),
            accountManager = accountManager,
        )

        result shouldBe NoProviderFeatures
    }
}
