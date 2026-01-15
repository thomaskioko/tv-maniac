package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.showdetails.PrefetchFirstSeasonInteractor.Param
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class PrefetchFirstSeasonInteractor(
    private val seasonsRepository: SeasonsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            val seasons = seasonsRepository.observeSeasonsByShowId(params.showTraktId).first()
            val firstSeason = seasons.minByOrNull { it.season_number } ?: return@withContext

            seasonDetailsRepository.fetchSeasonDetails(
                param = SeasonDetailsParam(
                    showTraktId = params.showTraktId,
                    seasonId = firstSeason.season_id.id,
                    seasonNumber = firstSeason.season_number,
                ),
                forceRefresh = params.forceRefresh,
            )
        }
    }

    public data class Param(
        val showTraktId: Long,
        val forceRefresh: Boolean = false,
    )
}
