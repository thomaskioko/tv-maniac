package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.db.GetSeasonWithShowInfo
import com.thomaskioko.tvmaniac.db.SeasonDetails
import com.thomaskioko.tvmaniac.db.SeasonImages
import kotlinx.coroutines.flow.Flow

public interface SeasonDetailsDao {
    public fun observeSeasonDetails(showTraktId: Long, seasonNumber: Long): Flow<List<SeasonDetails>>

    public fun observeSeasonWithShowInfo(showTraktId: Long, seasonNumber: Long): Flow<GetSeasonWithShowInfo?>

    public fun observeEpisodesBySeasonId(seasonId: Long): Flow<List<EpisodesBySeasonId>>

    public fun delete(showTraktId: Long)

    public fun deleteAll()

    public fun upsertSeasonImage(seasonId: Long, imageUrl: String)

    public fun fetchSeasonImages(id: Long): List<SeasonImages>

    public fun observeSeasonImages(id: Long): Flow<List<SeasonImages>>
}
