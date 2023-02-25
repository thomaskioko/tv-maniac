package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.db.EpisodeImage
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache

class EpisodeImageCacheImpl(
    private val database: TvManiacDatabase
) : EpisodeImageCache {

    private val episodeQueries get() = database.episodeImageQueries

    override fun insert(entity: EpisodeImage) {
        database.transaction {
            episodeQueries.insertOrReplace(
                trakt_id = entity.trakt_id,
                tmdb_id = entity.tmdb_id,
                image_url = entity.image_url
            )
        }
    }

    override fun insert(list: List<EpisodeImage>) {
        list.map { insert(it) }
    }

}
