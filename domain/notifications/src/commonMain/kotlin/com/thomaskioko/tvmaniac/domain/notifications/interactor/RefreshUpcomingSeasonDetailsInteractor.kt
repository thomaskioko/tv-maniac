package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.ApiRateLimiter
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.withRateLimitTracking
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

private const val SEASON_REFRESH_CONCURRENCY = 2

@Inject
@SingleIn(AppScope::class)
public class RefreshUpcomingSeasonDetailsInteractor(
    private val episodeRepository: EpisodeRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val apiRateLimiter: ApiRateLimiter,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<RefreshUpcomingSeasonDetailsInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        val upcomingEpisodes = episodeRepository.getUpcomingEpisodesFromFollowedShows(
            limit = params.limit,
        )

        if (upcomingEpisodes.isEmpty()) return

        withContext(dispatchers.io) {
            val uniqueSeasons = upcomingEpisodes
                .map { SeasonKey(it.showId, it.seasonId, it.seasonNumber) }
                .distinct()

            uniqueSeasons.parallelForEach(concurrency = SEASON_REFRESH_CONCURRENCY) { seasonKey ->
                apiRateLimiter.withRateLimitTracking {
                    seasonDetailsRepository.fetchSeasonDetails(
                        param = SeasonDetailsParam(
                            showTraktId = seasonKey.showTraktId,
                            seasonId = seasonKey.seasonId,
                            seasonNumber = seasonKey.seasonNumber,
                        ),
                        forceRefresh = params.forceRefresh,
                    )
                }
            }
        }
    }

    public data class Params(
        val limit: Duration = 12.hours,
        val forceRefresh: Boolean = false,
    )

    private data class SeasonKey(
        val showTraktId: Long,
        val seasonId: Long,
        val seasonNumber: Long,
    )
}
