package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveShowWatchProgressInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<Long, ShowWatchProgress>() {

    override fun createObservable(params: Long): Flow<ShowWatchProgress> =
        episodeRepository.observeShowWatchProgress(params)
}
