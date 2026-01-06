package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class ShowContentSyncInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<ShowContentSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            showDetailsRepository.fetchShowDetails(
                id = params.showId,
                forceRefresh = params.forceRefresh,
            )

            fetchShowSeasonDetails(params.showId, params.forceRefresh)

            watchedEpisodeSyncRepository.syncShowEpisodeWatches(
                showId = params.showId,
                forceRefresh = params.forceRefresh,
            )
        }
    }

    private suspend fun fetchShowSeasonDetails(showId: Long, forceRefresh: Boolean) {
        val seasons = seasonsRepository.observeSeasonsByShowId(showId).first()

        seasons.parallelForEach { season ->
            currentCoroutineContext().ensureActive()
            seasonDetailsRepository.fetchSeasonDetails(
                param = SeasonDetailsParam(
                    showId = showId,
                    seasonId = season.season_id.id,
                    seasonNumber = season.season_number,
                ),
                forceRefresh = forceRefresh,
            )
        }
    }

    public data class Param(
        val showId: Long,
        val forceRefresh: Boolean = false,
        val isUserInitiated: Boolean = false,
    )
}
