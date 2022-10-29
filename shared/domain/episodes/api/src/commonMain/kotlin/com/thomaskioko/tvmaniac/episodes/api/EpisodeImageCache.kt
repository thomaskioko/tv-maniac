package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.EpisodeImage

interface EpisodeImageCache {

    fun insert(entity: EpisodeImage)

    fun insert(list: List<EpisodeImage>)

}
