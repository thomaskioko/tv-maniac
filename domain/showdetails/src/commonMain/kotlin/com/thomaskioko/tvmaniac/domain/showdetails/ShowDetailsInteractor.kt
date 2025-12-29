package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor.Param
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class ShowDetailsInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {
    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            showDetailsRepository.fetchShowDetails(id = params.id, forceRefresh = params.forceRefresh)
            fetchFirstSeasonIfNeeded(params.id, params.forceRefresh)
        }
    }

    private suspend fun fetchFirstSeasonIfNeeded(showId: Long, forceRefresh: Boolean) {
        val seasons = seasonsRepository.observeSeasonsByShowId(showId, includeSpecials = false).first()
        val firstSeason = seasons.minByOrNull { it.season_number } ?: return

        seasonDetailsRepository.fetchSeasonDetails(
            param = SeasonDetailsParam(
                showId = showId,
                seasonId = firstSeason.season_id.id,
                seasonNumber = firstSeason.season_number,
            ),
            forceRefresh = forceRefresh,
        )
    }

    public data class Param(val id: Long, val forceRefresh: Boolean = false)
}
