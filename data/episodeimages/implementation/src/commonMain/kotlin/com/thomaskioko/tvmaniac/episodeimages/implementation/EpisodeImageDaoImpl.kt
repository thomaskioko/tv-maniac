package com.thomaskioko.tvmaniac.episodeimages.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.EpisodeImage
import com.thomaskioko.tvmaniac.core.db.Episode_image
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class EpisodeImageDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : EpisodeImageDao {

    private val episodeQueries get() = database.episode_imageQueries

    override fun insert(entity: Episode_image) {
        episodeQueries.insertOrReplace(
            trakt_id = entity.trakt_id,
            tmdb_id = entity.tmdb_id,
            image_url = entity.image_url,
        )
    }

    override fun insert(list: List<Episode_image>) {
        database.transaction {
            list.map { insert(it) }
        }
    }

    override fun observeEpisodeImage(): Flow<List<EpisodeImage>> =
        episodeQueries.episodeImage()
            .asFlow()
            .mapToList(dispatchers.io)
}
