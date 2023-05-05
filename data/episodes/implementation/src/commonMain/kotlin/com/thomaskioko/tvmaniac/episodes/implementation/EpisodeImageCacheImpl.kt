package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.db.Episode_image
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import me.tatarka.inject.annotations.Inject

@Inject
class EpisodeImageCacheImpl(
    private val database: TvManiacDatabase,
) : EpisodeImageCache {

    private val episodeQueries get() = database.episode_imageQueries

    override fun insert(entity: Episode_image) {
        database.transaction {
            episodeQueries.insertOrReplace(
                trakt_id = entity.trakt_id,
                tmdb_id = entity.tmdb_id,
                image_url = entity.image_url,
            )
        }
    }

    override fun insert(list: List<Episode_image>) {
        list.map { insert(it) }
    }
}
