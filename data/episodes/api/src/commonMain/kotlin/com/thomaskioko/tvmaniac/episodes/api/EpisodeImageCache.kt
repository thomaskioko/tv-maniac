package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.Episode_image

interface EpisodeImageCache {

    fun insert(entity: Episode_image)

    fun insert(list: List<Episode_image>)
}
