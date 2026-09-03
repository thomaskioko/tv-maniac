package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.ResultInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import dev.zacsweers.metro.Inject

@Inject
public class ResolveEpisodeIdInteractor(
    private val episodeRepository: EpisodeRepository,
) : ResultInteractor<ResolveEpisodeIdParams, Long?>() {

    override suspend fun doWork(params: ResolveEpisodeIdParams): Long? =
        episodeRepository.getEpisodeId(
            showId = params.showId,
            seasonNumber = params.seasonNumber,
            episodeNumber = params.episodeNumber,
        )
}

public data class ResolveEpisodeIdParams(
    val showId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
)
