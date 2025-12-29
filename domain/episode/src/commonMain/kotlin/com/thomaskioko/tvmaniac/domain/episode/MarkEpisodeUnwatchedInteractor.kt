package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class MarkEpisodeUnwatchedInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<MarkEpisodeUnwatchedParams>() {

    override suspend fun doWork(params: MarkEpisodeUnwatchedParams) {
        episodeRepository.markEpisodeAsUnwatched(
            showId = params.showId,
            episodeId = params.episodeId,
        )
    }
}

public data class MarkEpisodeUnwatchedParams(
    val showId: Long,
    val episodeId: Long,
)
