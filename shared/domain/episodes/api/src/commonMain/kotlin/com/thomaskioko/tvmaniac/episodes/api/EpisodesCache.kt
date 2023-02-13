package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.EpisodeArtByShowId
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

interface EpisodesCache {

    fun insert(entity: EpisodeCache)

    fun insert(list: List<EpisodeCache>)

    fun observeEpisodeArtByShowId(id: Long): List<EpisodeArtByShowId>
}
