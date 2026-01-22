package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.db.SeasonImages
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.flow.Flow

public interface SeasonDetailsRepository {
    public suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
        forceRefresh: Boolean = false,
    )

    public fun observeSeasonDetails(
        param: SeasonDetailsParam,
    ): Flow<SeasonDetailsWithEpisodes>

    public fun observeSeasonImages(id: Long): Flow<List<SeasonImages>>
}
