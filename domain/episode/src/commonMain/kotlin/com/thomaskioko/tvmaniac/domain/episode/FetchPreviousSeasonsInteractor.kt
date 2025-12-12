package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import me.tatarka.inject.annotations.Inject

@Inject
class FetchPreviousSeasonsInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<FetchPreviousSeasonsParams>() {
    override suspend fun doWork(params: FetchPreviousSeasonsParams) {
        episodeRepository.getUnwatchedCountAfterFetchingPreviousSeasons(
            showId = params.showId,
            seasonNumber = params.seasonNumber,
        )
    }
}

data class FetchPreviousSeasonsParams(
    val showId: Long,
    val seasonNumber: Long,
)
