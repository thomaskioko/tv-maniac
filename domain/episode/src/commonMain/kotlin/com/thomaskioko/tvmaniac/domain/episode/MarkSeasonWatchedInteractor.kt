package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class MarkSeasonWatchedInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<MarkSeasonWatchedParams>() {

    override suspend fun doWork(params: MarkSeasonWatchedParams) {
        if (params.markPreviousSeasons) {
            episodeRepository.markSeasonAndPreviousSeasonsWatched(
                showTraktId = params.showTraktId,
                seasonNumber = params.seasonNumber,
            )
        } else {
            episodeRepository.markSeasonWatched(
                showTraktId = params.showTraktId,
                seasonNumber = params.seasonNumber,
            )
        }
    }
}

public data class MarkSeasonWatchedParams(
    val showTraktId: Long,
    val seasonNumber: Long,
    val markPreviousSeasons: Boolean = false,
)
