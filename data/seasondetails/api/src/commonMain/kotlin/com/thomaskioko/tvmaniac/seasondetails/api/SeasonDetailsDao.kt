package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.db.Season_images
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsDao {
    fun fetchSeasonDetails(showId: Long, seasonNumber: Long): SeasonDetailsWithEpisodes?

    fun observeSeasonEpisodeDetails(showId: Long, seasonNumber: Long): Flow<SeasonDetailsWithEpisodes?>

    fun delete(id: Long)

    fun deleteAll()

    fun upsertSeasonImage(seasonId: Long, imageUrl: String)

    fun fetchSeasonImages(id: Long): List<Season_images>

    fun observeSeasonImages(id: Long): Flow<List<Season_images>>
}
