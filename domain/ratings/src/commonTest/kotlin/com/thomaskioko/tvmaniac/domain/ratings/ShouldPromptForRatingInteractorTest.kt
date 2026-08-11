package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.data.ratings.api.EpisodeRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionManager
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ShouldPromptForRatingInteractorTest {

    private val datastoreRepository = FakeDatastoreRepository()
    private val subscriptionManager = FakeSubscriptionManager()
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = ShouldPromptForRatingInteractor(
        datastoreRepository = datastoreRepository,
        subscriptionManager = subscriptionManager,
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should prompt given the setting is on and the episode is unrated`() = runTest {
        datastoreRepository.saveQuickRateEnabled(true)

        interactor(param(FIRST_EPISODE)) shouldBe true
    }

    @Test
    fun `should not prompt given the setting is off`() = runTest {
        interactor(param(FIRST_EPISODE)) shouldBe false
    }

    @Test
    fun `should not prompt given the subscription does not cover quick rate`() = runTest {
        datastoreRepository.saveQuickRateEnabled(true)
        subscriptionManager.setAccess(SubscriptionFeature.QuickRate, false)

        interactor(param(FIRST_EPISODE)) shouldBe false
    }

    @Test
    fun `should not prompt given the episode already carries a rating`() = runTest {
        datastoreRepository.saveQuickRateEnabled(true)
        ratingsRepository.setEpisodeRating(FIRST_EPISODE, rated(8))

        interactor(param(FIRST_EPISODE)) shouldBe false
    }

    @Test
    fun `should quiet the show for the session given the previous prompt went unanswered`() = runTest {
        datastoreRepository.saveQuickRateEnabled(true)

        interactor(param(FIRST_EPISODE)) shouldBe true
        interactor(param(SECOND_EPISODE)) shouldBe false
        interactor(param(THIRD_EPISODE)) shouldBe false
    }

    @Test
    fun `should keep prompting given the previous prompt was answered`() = runTest {
        datastoreRepository.saveQuickRateEnabled(true)

        interactor(param(FIRST_EPISODE)) shouldBe true
        ratingsRepository.setEpisodeRating(FIRST_EPISODE, rated(9))

        interactor(param(SECOND_EPISODE)) shouldBe true
    }

    @Test
    fun `should keep prompting for another show given one show has gone quiet`() = runTest {
        datastoreRepository.saveQuickRateEnabled(true)

        interactor(param(FIRST_EPISODE)) shouldBe true
        interactor(param(SECOND_EPISODE)) shouldBe false

        interactor(
            ShouldPromptForRatingInteractor.Param(showId = OTHER_SHOW_ID, episodeId = OTHER_SHOW_EPISODE),
        ) shouldBe true
    }

    private fun param(episodeId: Long) = ShouldPromptForRatingInteractor.Param(
        showId = SHOW_ID,
        episodeId = episodeId,
    )

    private fun rated(stars: Int) = EpisodeRating(userRating = stars, pendingAction = PendingAction.NOTHING)

    private companion object {
        const val SHOW_ID = 1L
        const val OTHER_SHOW_ID = 2L
        const val FIRST_EPISODE = 10L
        const val SECOND_EPISODE = 11L
        const val THIRD_EPISODE = 12L
        const val OTHER_SHOW_EPISODE = 20L
    }
}
