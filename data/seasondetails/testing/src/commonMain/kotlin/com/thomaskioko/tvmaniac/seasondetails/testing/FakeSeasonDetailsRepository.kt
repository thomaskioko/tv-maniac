package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.db.Season_images
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private val seasonsResult = MutableStateFlow<SeasonDetailsWithEpisodes?>(
        SeasonDetailsWithEpisodes(
            seasonId = 0,
            tvShowId = 0,
            name = "",
            showTitle = "",
            seasonOverview = "",
            imageUrl = "",
            seasonNumber = 0,
            episodeCount = 0,
            episodes = emptyList(),
        ),
    )

    suspend fun setSeasonsResult(result: SeasonDetailsWithEpisodes?) {
        seasonsResult.emit(result)
    }

    override suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
        forceRefresh: Boolean,
    ) {
    }

    override fun observeSeasonDetails(param: SeasonDetailsParam): Flow<SeasonDetailsWithEpisodes?> = seasonsResult.asStateFlow()

    override fun observeSeasonImages(id: Long): Flow<List<Season_images>> = flowOf(emptyList())
}
