package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.showdetails.FetchSeasonsEpisodesInteractor.Param
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsEpisodesSyncRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class FetchSeasonsEpisodesInteractor(
    private val seasonsEpisodesSyncRepository: SeasonsEpisodesSyncRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            seasonsEpisodesSyncRepository.syncSeasonsWithEpisodes(showId = params.id, forceRefresh = params.forceRefresh)
            seasonDetailsRepository.syncShowSeasonDetails(showId = params.id, forceRefresh = params.forceRefresh)
            watchedEpisodeSyncRepository.syncShowEpisodeWatches(showId = params.id, forceRefresh = params.forceRefresh)
        }
    }

    public data class Param(val id: Long, val forceRefresh: Boolean = false)
}
