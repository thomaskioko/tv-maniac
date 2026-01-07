package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.GetEpisodeByShowSeasonEpisodeNumber
import com.thomaskioko.tvmaniac.db.Episode as EpisodeCache

public interface EpisodesDao {

    public fun insert(entity: EpisodeCache)

    public fun insert(list: List<EpisodeCache>)

    public fun delete(id: Long)

    public fun deleteAll()

    public suspend fun getEpisodeByShowSeasonEpisodeNumber(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): GetEpisodeByShowSeasonEpisodeNumber?
}
