package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Inject
@SingleIn(AppScope::class)
public class ShouldPromptForRatingInteractor(
    private val datastoreRepository: DatastoreRepository,
    private val subscriptionManager: SubscriptionManager,
    private val ratingsRepository: RatingsRepository,
) {

    private val mutex = Mutex()
    private val quietShows = mutableSetOf<Long>()
    private val lastAskedEpisode = mutableMapOf<Long, Long>()

    public suspend operator fun invoke(params: Param): Boolean = mutex.withLock {
        if (!datastoreRepository.observeQuickRateEnabled().first()) return false
        if (!subscriptionManager.hasAccess(SubscriptionFeature.QuickRate)) return false
        if (params.showId in quietShows) return false
        if (userRatingOf(params.episodeId) != null) return false

        val previouslyAsked = lastAskedEpisode[params.showId]
        if (previouslyAsked != null && userRatingOf(previouslyAsked) == null) {
            quietShows += params.showId
            return false
        }

        lastAskedEpisode[params.showId] = params.episodeId
        return true
    }

    private suspend fun userRatingOf(episodeId: Long): Int? =
        ratingsRepository.observeEpisodeRating(episodeId).first().userRating

    public data class Param(
        val showId: Long,
        val episodeId: Long,
    )
}
