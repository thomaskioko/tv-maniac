package com.thomaskioko.tvmaniac.lastairepisodes.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.Last_episode
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import kotlinx.coroutines.flow.Flow

class LastEpisodeAirCacheImpl(
    private val database: TvManiacDatabase
) : LastEpisodeAirCache {

    override fun insert(episode: Last_episode) {
        database.lastAirEpisodeQueries.transaction {
            database.lastAirEpisodeQueries.insertOrReplace(
                id = episode.id,
                show_id = episode.show_id,
                name = episode.name,
                overview = episode.overview,
                title = episode.title,
                air_date = episode.air_date,
                episode_number = episode.episode_number,
                season_number = episode.season_number,
                still_path = episode.still_path,
                vote_average = episode.vote_average,
                vote_count = episode.vote_count
            )
        }
    }

    override fun insert(list: List<Last_episode>) {
        list.forEach { insert(it) }
    }

    override fun getShowAirEpisodes(showId: Int): Flow<List<AirEpisodesByShowId>> =
        database.lastAirEpisodeQueries.airEpisodesByShowId(show_id = showId)
            .asFlow()
            .mapToList()
}
