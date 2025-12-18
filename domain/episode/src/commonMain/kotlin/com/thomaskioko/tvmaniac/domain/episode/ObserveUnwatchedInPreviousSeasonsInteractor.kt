package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveUnwatchedInPreviousSeasonsInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<ObserveUnwatchedInPreviousSeasonsParams, Boolean>() {

    override fun createObservable(params: ObserveUnwatchedInPreviousSeasonsParams): Flow<Boolean> {
        return episodeRepository.observeUnwatchedCountInPreviousSeasons(
            showId = params.showId,
            seasonNumber = params.seasonNumber,
        )
            .map { it > 0 }
    }
}

data class ObserveUnwatchedInPreviousSeasonsParams(
    val showId: Long,
    val seasonNumber: Long,
)
