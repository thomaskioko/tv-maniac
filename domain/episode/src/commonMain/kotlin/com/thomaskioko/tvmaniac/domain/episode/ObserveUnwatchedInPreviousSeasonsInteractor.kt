package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveUnwatchedInPreviousSeasonsInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<ObserveUnwatchedInPreviousSeasonsParams, Boolean>() {

    override fun createObservable(params: ObserveUnwatchedInPreviousSeasonsParams): Flow<Boolean> {
        return episodeRepository.observeUnwatchedCountInPreviousSeasons(
            showTraktId = params.showTraktId,
            seasonNumber = params.seasonNumber,
        )
            .map { it > 0 }
    }
}

public data class ObserveUnwatchedInPreviousSeasonsParams(
    val showTraktId: Long,
    val seasonNumber: Long,
)
