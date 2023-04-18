package com.thomaskioko.tvmaniac.tmdb.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Show_image
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ShowImageCacheImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowImageCache {

    override fun insert(image: Show_image) {
        database.transaction {
            database.show_imageQueries.insertOrReplace(
                trakt_id = image.trakt_id,
                tmdb_id = image.tmdb_id,
                poster_url = image.poster_url,
                backdrop_url = image.backdrop_url
            )
        }
    }

    override fun observeShowArt(): Flow<List<Show_image>> =
        database.show_imageQueries.selectImages()
            .asFlow()
            .mapToList(dispatchers.io)
}