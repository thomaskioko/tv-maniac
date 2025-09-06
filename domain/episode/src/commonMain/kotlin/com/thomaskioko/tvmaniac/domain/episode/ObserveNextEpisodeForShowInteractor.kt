package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveNextEpisodeForShowInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<Long, NextEpisodeWithShow?>() {

    override fun createObservable(params: Long): Flow<NextEpisodeWithShow?> {
        return episodeRepository.observeNextEpisodeForShow(params)
    }
}
