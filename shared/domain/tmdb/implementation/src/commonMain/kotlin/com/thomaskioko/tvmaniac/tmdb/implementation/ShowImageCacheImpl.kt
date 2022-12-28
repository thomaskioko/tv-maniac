package com.thomaskioko.tvmaniac.tmdb.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Show_image
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import kotlinx.coroutines.flow.Flow

class ShowImageCacheImpl(
    private val database: TvManiacDatabase
) : ShowImageCache {

    override fun insert(image: Show_image) {
        database.transaction {
            database.showImageQueries.insertOrReplace(
                trakt_id = image.trakt_id,
                tmdb_id = image.tmdb_id,
                poster_url = image.poster_url,
                backdrop_url = image.backdrop_url
            )
        }
    }

    override fun observeShowArt(): Flow<List<Show_image>> =
        database.showImageQueries.selectImages()
            .asFlow()
            .mapToList()
}