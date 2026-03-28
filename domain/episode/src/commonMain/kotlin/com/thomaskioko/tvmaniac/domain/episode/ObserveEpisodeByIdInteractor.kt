package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveEpisodeByIdInteractor(
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<Long, EpisodeById?>() {

    override fun createObservable(params: Long): Flow<EpisodeById?> =
        episodeRepository.observeEpisodeById(params)
}
