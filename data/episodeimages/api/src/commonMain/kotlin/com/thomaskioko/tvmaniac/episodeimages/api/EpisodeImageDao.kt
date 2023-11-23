package com.thomaskioko.tvmaniac.episodeimages.api

import com.thomaskioko.tvmaniac.core.db.EpisodeImage
import com.thomaskioko.tvmaniac.core.db.Episode_image
import kotlinx.coroutines.flow.Flow

interface EpisodeImageDao {

    fun upsert(entity: Episode_image)

    fun upsert(list: List<Episode_image>)

    fun observeEpisodeImage(showId: Long): Flow<List<EpisodeImage>>

    fun getEpisodeImage(showId: Long): List<EpisodeImage>

    fun delete(id: Long)

    fun deleteAll()
}
