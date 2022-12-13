package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

    fun observeShowSeasons(traktId: Int): Flow<Resource<List<SelectSeasonsByShowId>>>

    fun updateSeasonEpisodes(
        showId: Int,
    ): Flow<Resource<List<SelectSeasonWithEpisodes>>>

    fun observeSeasonEpisodes(showId: Int): Flow<List<SelectSeasonWithEpisodes>>
}
