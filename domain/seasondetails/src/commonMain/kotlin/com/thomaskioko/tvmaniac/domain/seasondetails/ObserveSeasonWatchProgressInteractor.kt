package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveSeasonWatchProgressInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<ObserveSeasonWatchProgressParams, SeasonWatchProgress>() {

    override fun createObservable(params: ObserveSeasonWatchProgressParams): Flow<SeasonWatchProgress> {
        return episodeRepository.observeSeasonWatchProgress(
            showId = params.showId,
            seasonNumber = params.seasonNumber,
        )
    }
}

public data class ObserveSeasonWatchProgressParams(
    val showId: Long,
    val seasonNumber: Long,
)
