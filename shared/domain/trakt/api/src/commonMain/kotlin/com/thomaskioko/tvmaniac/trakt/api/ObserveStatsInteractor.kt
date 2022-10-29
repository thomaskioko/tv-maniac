package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveStatsInteractor constructor(
    private val repository: TraktRepository
) : FlowInteractor<StatsParam, TraktStatsResult?>() {

    override fun run(params: StatsParam): Flow<TraktStatsResult?> =
        repository.observeStats(params.slug, params.refresh).map { result ->
            result.data?.let {
                TraktStatsResult(
                    showHours = it.hours.padStart(2, '0'),
                    showDays = it.days.padStart(2, '0'),
                    showMonths = it.months.padStart(2, '0'),
                    collectedShows = it.collected_shows,
                    episodesWatched = it.episodes_watched
                )
            }
        }
}


data class StatsParam(
    val refresh: Boolean = false,
    val slug: String
)

data class TraktStatsResult(
    val showHours: String,
    val showDays: String,
    val showMonths: String,
    val collectedShows: String,
    val episodesWatched: String
)