package com.thomaskioko.tvmaniac.showimages.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.SelectShowImages
import com.thomaskioko.tvmaniac.core.db.Show_image
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ShowImagesDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowImagesDao {

    override fun insert(image: Show_image) {
        database.transaction {
            database.show_imageQueries.insertOrReplace(
                trakt_id = image.trakt_id,
                tmdb_id = image.tmdb_id,
                poster_url = image.poster_url,
                backdrop_url = image.backdrop_url,
            )
        }
    }

    override fun observeShowImages(): Flow<List<SelectShowImages>> {
        return database.show_imageQueries.selectShowImages()
            .asFlow()
            .mapToList(dispatchers.io)
    }
}
