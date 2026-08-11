package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * Drains pending rating writes by observing local rating state and pushing to the active provider
 * whenever an unsynced rating appears. Keeps rate/remove interactors single-responsibility: they only
 * mutate local state, and this reactive drain owns the sync.
 *
 * Also reads the ratings already held by the active provider once per launch, so statistics and show
 * details show what was rated elsewhere without waiting for each show to be opened.
 */
@Inject
public class RatingsSyncInitializer(
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    ratingsRepositoryLazy: Lazy<RatingsRepository>,
) {

    private val ratingsRepository by ratingsRepositoryLazy

    public fun init() {
        coroutineScope.launch {
            ratingsRepository.syncUserRatings()
        }

        coroutineScope.launch {
            ratingsRepository.observePendingRatings()
                .filter { it }
                .collect { ratingsRepository.syncPendingRatings() }
        }
    }
}
