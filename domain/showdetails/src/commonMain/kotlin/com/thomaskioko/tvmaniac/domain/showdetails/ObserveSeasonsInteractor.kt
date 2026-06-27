package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.showdetails.model.Season
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

@Inject
public class ObserveSeasonsInteractor(
    private val seasonsRepository: SeasonsRepository,
    private val episodeRepository: EpisodeRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, List<Season>>() {

    override fun createObservable(params: Long): Flow<List<Season>> =
        combine(
            seasonsRepository.observeSeasonsByShowId(params),
            episodeRepository.observeAllSeasonsWatchProgress(params),
        ) { seasons, progress ->
            val progressMap = progress.associateBy { it.seasonNumber }
            seasons.toSeasonsList(progressMap)
        }.flowOn(dispatchers.io)
}
