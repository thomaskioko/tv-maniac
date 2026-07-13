package com.thomaskioko.tvmaniac.domain.settings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ObserveLayoutPreferencesInteractorTest {

    private val datastoreRepository = FakeDatastoreRepository()
    private val interactor = ObserveLayoutPreferencesInteractor(datastoreRepository)

    @Test
    fun `should emit haptic feedback enabled by default`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().hapticFeedbackEnabled shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit saved haptic feedback value given the preference changes`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().hapticFeedbackEnabled shouldBe true

            datastoreRepository.saveHapticFeedbackEnabled(false)

            awaitItem().hapticFeedbackEnabled shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit blur unwatched episode images disabled by default`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().blurImage shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit saved blur value given the preference changes`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().blurImage shouldBe false

            datastoreRepository.saveBlurUnwatchedEpisodeImages(true)

            awaitItem().blurImage shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit an empty hidden discover sections set by default`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().hiddenDiscoverSections shouldBe emptySet()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit saved hidden discover sections given the preference changes`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().hiddenDiscoverSections shouldBe emptySet()

            datastoreRepository.saveHiddenDiscoverSections(
                setOf(DiscoverSection.UPCOMING, DiscoverSection.POPULAR),
            )

            awaitItem().hiddenDiscoverSections shouldBe setOf(DiscoverSection.UPCOMING, DiscoverSection.POPULAR)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit a font size of 100 percent by default`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().fontSizePercent shouldBe 100
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit saved font size given the preference changes`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().fontSizePercent shouldBe 100

            datastoreRepository.saveFontSizePercent(120)

            awaitItem().fontSizePercent shouldBe 120
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit standard poster widths and sharp corners by default`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            val preferences = awaitItem()
            preferences.posterWidth shouldBe PosterWidth.STANDARD
            preferences.landscapeWidth shouldBe PosterWidth.STANDARD
            preferences.posterCornerStyle shouldBe PosterCornerStyle.SHARP
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit saved poster style values given the preferences change`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem()

            datastoreRepository.savePosterWidth(PosterWidth.LARGE)
            awaitItem().posterWidth shouldBe PosterWidth.LARGE

            datastoreRepository.saveLandscapeWidth(PosterWidth.COMPACT)
            awaitItem().landscapeWidth shouldBe PosterWidth.COMPACT

            datastoreRepository.savePosterCornerStyle(PosterCornerStyle.PILL)
            awaitItem().posterCornerStyle shouldBe PosterCornerStyle.PILL
            cancelAndIgnoreRemainingEvents()
        }
    }
}
