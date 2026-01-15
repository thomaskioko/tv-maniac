package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class FetchPreviousSeasonsInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<FetchPreviousSeasonsParams>() {
    override suspend fun doWork(params: FetchPreviousSeasonsParams) {
        val _ = episodeRepository.getUnwatchedCountAfterFetchingPreviousSeasons(
            showTraktId = params.showTraktId,
            seasonNumber = params.seasonNumber,
        )
    }
}

public data class FetchPreviousSeasonsParams(
    val showTraktId: Long,
    val seasonNumber: Long,
)
