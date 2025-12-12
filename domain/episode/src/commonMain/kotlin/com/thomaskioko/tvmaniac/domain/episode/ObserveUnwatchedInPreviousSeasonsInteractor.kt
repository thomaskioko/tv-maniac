package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveUnwatchedInPreviousSeasonsInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<ObserveUnwatchedInPreviousSeasonsParams, Long>() {

    override fun createObservable(params: ObserveUnwatchedInPreviousSeasonsParams): Flow<Long> {
        return episodeRepository.observeUnwatchedCountInPreviousSeasons(
            showId = params.showId,
            seasonNumber = params.seasonNumber,
        )
    }
}

data class ObserveUnwatchedInPreviousSeasonsParams(
    val showId: Long,
    val seasonNumber: Long,
)
