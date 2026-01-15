package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class MarkSeasonUnwatchedInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<MarkSeasonUnwatchedParams>() {

    override suspend fun doWork(params: MarkSeasonUnwatchedParams) {
        episodeRepository.markSeasonUnwatched(
            showTraktId = params.showTraktId,
            seasonNumber = params.seasonNumber,
        )
    }
}

public data class MarkSeasonUnwatchedParams(
    val showTraktId: Long,
    val seasonNumber: Long,
)
