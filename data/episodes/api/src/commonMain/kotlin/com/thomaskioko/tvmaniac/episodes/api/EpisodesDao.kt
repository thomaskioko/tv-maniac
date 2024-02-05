package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

interface EpisodesDao {

  fun insert(entity: EpisodeCache)

  fun insert(list: List<EpisodeCache>)

  fun delete(id: Long)

  fun deleteAll()
}
