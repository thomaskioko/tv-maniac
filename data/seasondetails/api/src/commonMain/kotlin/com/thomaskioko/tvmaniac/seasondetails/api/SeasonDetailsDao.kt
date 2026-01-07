package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.db.SeasonImages
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.flow.Flow

public interface SeasonDetailsDao {
    public fun fetchSeasonDetails(showId: Long, seasonNumber: Long): SeasonDetailsWithEpisodes?

    public fun observeSeasonEpisodeDetails(showId: Long, seasonNumber: Long): Flow<SeasonDetailsWithEpisodes?>

    public fun delete(id: Long)

    public fun deleteAll()

    public fun upsertSeasonImage(seasonId: Long, imageUrl: String)

    public fun fetchSeasonImages(id: Long): List<SeasonImages>

    public fun observeSeasonImages(id: Long): Flow<List<SeasonImages>>
}
