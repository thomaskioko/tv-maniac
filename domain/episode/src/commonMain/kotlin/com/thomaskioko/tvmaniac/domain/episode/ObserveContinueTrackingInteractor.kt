package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveContinueTrackingInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<Long, ContinueTrackingResult?>() {

    override fun createObservable(params: Long): Flow<ContinueTrackingResult?> =
        episodeRepository.observeContinueTrackingEpisodes(params)
}
