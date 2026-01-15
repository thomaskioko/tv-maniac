package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.db.GetSeasonByShowAndNumber
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.ShowSeasons
import kotlinx.coroutines.flow.Flow

public interface SeasonsDao {

    public fun upsert(season: Season)

    public fun upsert(entityList: List<Season>)

    public fun fetchShowSeasons(showTraktId: Long, includeSpecials: Boolean = true): List<ShowSeasons>

    public fun observeSeasonsByShowTraktId(showTraktId: Long, includeSpecials: Boolean = true): Flow<List<ShowSeasons>>

    public suspend fun getSeasonByShowAndNumber(showTraktId: Long, seasonNumber: Long): GetSeasonByShowAndNumber?

    public fun updateImageUrl(seasonId: Long, imageUrl: String)

    public fun delete(showTraktId: Long)

    public fun deleteAll()
}
