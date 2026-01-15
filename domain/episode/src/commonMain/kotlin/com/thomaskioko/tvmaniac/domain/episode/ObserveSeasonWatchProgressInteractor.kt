package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveSeasonWatchProgressInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<ObserveSeasonWatchProgressParams, SeasonWatchProgress>() {

    override fun createObservable(params: ObserveSeasonWatchProgressParams): Flow<SeasonWatchProgress> {
        return episodeRepository.observeSeasonWatchProgress(
            showTraktId = params.showTraktId,
            seasonNumber = params.seasonNumber,
        )
    }
}

public data class ObserveSeasonWatchProgressParams(
    val showTraktId: Long,
    val seasonNumber: Long,
)
