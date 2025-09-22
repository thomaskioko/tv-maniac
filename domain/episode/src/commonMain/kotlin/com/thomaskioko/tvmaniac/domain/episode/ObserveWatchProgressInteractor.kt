package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveWatchProgressInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<Long, WatchProgress>() {

    override fun createObservable(params: Long): Flow<WatchProgress> {
        return episodeRepository.observeWatchProgress(params)
    }
}
