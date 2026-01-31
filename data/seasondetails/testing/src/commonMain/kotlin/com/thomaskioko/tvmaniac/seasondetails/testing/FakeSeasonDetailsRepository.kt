package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.db.SeasonImages
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

public class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private val seasonsResult = MutableStateFlow(
        SeasonDetailsWithEpisodes(
            seasonId = 0,
            showTraktId = 0,
            showTmdbId = 0,
            name = "",
            showTitle = "",
            seasonOverview = "",
            imageUrl = "",
            seasonNumber = 0,
            episodeCount = 0,
            episodes = emptyList(),
        ),
    )

    private val continueTrackingResult = MutableStateFlow<ContinueTrackingResult?>(null)

    public suspend fun setSeasonsResult(result: SeasonDetailsWithEpisodes) {
        seasonsResult.emit(result)
        continueTrackingResult.emit(
            ContinueTrackingResult(
                episodes = result.episodes.toImmutableList(),
                currentSeasonNumber = result.seasonNumber,
                currentSeasonId = result.seasonId,
            ),
        )
    }

    public fun setContinueTrackingResult(result: ContinueTrackingResult?) {
        continueTrackingResult.value = result
    }

    override suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
        forceRefresh: Boolean,
    ) {
    }

    override suspend fun syncPreviousSeasonsEpisodes(
        showTraktId: Long,
        beforeSeasonNumber: Long,
        forceRefresh: Boolean,
    ) {
    }

    override fun observeSeasonDetails(param: SeasonDetailsParam): Flow<SeasonDetailsWithEpisodes> = seasonsResult.asStateFlow()

    override fun observeSeasonImages(id: Long): Flow<List<SeasonImages>> = flowOf(emptyList())

    override fun observeContinueTrackingEpisodes(showTraktId: Long): Flow<ContinueTrackingResult?> =
        continueTrackingResult.asStateFlow()
}
