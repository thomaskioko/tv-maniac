package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor.Param
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsEpisodesSyncRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Inject
public class ShowDetailsInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val castRepository: CastRepository,
    private val trailerRepository: TrailerRepository,
    private val providerRepository: WatchProviderRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val seasonsEpisodesSyncRepository: SeasonsEpisodesSyncRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            coroutineScope {
                launch { castRepository.fetchShowCast(showTraktId = params.id, forceRefresh = params.forceRefresh) }
                launch { trailerRepository.fetchTrailers(traktId = params.id, forceRefresh = params.forceRefresh) }
                launch { providerRepository.fetchWatchProviders(traktId = params.id, forceRefresh = params.forceRefresh) }
                launch {
                    showDetailsRepository.fetchShowDetails(id = params.id, forceRefresh = params.forceRefresh)
                    seasonsEpisodesSyncRepository.syncSeasonsWithEpisodes(showTraktId = params.id, forceRefresh = params.forceRefresh)
                    seasonDetailsRepository.syncShowSeasonDetails(showTraktId = params.id, forceRefresh = params.forceRefresh)
                    watchedEpisodeSyncRepository.syncShowEpisodeWatches(showTraktId = params.id, forceRefresh = params.forceRefresh)
                }
            }
        }
    }

    public data class Param(val id: Long, val forceRefresh: Boolean = false)
}
