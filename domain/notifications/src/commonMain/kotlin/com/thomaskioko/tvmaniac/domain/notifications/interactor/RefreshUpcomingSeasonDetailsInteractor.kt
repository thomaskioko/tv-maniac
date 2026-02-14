package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.ApiRateLimiter
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.withRateLimitTracking
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

private const val SEASON_REFRESH_CONCURRENCY = 2

@Inject
@SingleIn(AppScope::class)
public class RefreshUpcomingSeasonDetailsInteractor(
    private val seasonsRepository: SeasonsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val apiRateLimiter: ApiRateLimiter,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<RefreshUpcomingSeasonDetailsInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        val latestSeasons = seasonsRepository.getLatestSeasonsForFollowedShows()

        if (latestSeasons.isEmpty()) return

        withContext(dispatchers.io) {
            latestSeasons.parallelForEach(concurrency = SEASON_REFRESH_CONCURRENCY) { season ->
                apiRateLimiter.withRateLimitTracking {
                    seasonDetailsRepository.fetchSeasonDetails(
                        param = SeasonDetailsParam(
                            showTraktId = season.showTraktId,
                            seasonId = season.seasonId,
                            seasonNumber = season.seasonNumber,
                        ),
                        forceRefresh = params.forceRefresh,
                    )
                }
            }
        }
    }

    public data class Params(
        val forceRefresh: Boolean = false,
    )
}
