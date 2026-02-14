package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.db.ShowSeasons
import kotlinx.coroutines.flow.Flow

public interface SeasonsRepository {
    public fun observeSeasonsByShowId(id: Long): Flow<List<ShowSeasons>>

    public fun getSeasonsByShowId(id: Long, includeSpecials: Boolean = false): List<ShowSeasons>

    public suspend fun getLatestSeasonsForFollowedShows(): List<FollowedShowSeason>
}
