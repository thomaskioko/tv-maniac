package com.thomaskioko.tvmaniac.showimages.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.EmptyShowImage
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

    override fun upsert(image: Show_image) {
        database.transaction {
            database.show_imageQueries.insertOrReplace(
                id = image.id,
                tmdb_id = image.tmdb_id,
                poster_url = image.poster_url,
                backdrop_url = image.backdrop_url,
            )
        }
    }

    override fun observeShowImages(): Flow<List<EmptyShowImage>> {
        return database.show_imageQueries.emptyShowImage()
            .asFlow()
            .mapToList(dispatchers.io)
    }
}
