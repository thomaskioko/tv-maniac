package com.thomaskioko.tvmaniac.episodeimages.api

import com.thomaskioko.tvmaniac.core.db.EpisodeImage
import com.thomaskioko.tvmaniac.core.db.Episode_image
import kotlinx.coroutines.flow.Flow

interface EpisodeImageDao {

    fun insert(entity: Episode_image)

    fun insert(list: List<Episode_image>)

    fun observeEpisodeImage(): Flow<List<EpisodeImage>>
}
