package com.thomaskioko.tvmaniac.shows.implementation.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShowImages
import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ShowCacheImpl constructor(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowsCache {

    override fun insert(show: Show) {
        database.showQueries.transaction {
            database.showQueries.insertOrReplace(
                trakt_id = show.trakt_id,
                tmdb_id = show.tmdb_id,
                title = show.title,
                overview = show.overview,
                language = show.language,
                votes = show.votes,
                year = show.year,
                status = show.status,
                runtime = show.runtime,
                rating = show.rating,
                genres = show.genres
            )
        }
    }

    override fun insert(list: List<Show>) {
        list.forEach { insert(it) }
    }

    override fun observeTvShow(showId: Long): Flow<SelectByShowId> {
        return database.showQueries.selectByShowId(showId)
            .asFlow()
            .mapToOne(dispatchers.io)
    }

    override fun observeTvShows(): Flow<List<SelectShows>> {
        return database.showQueries.selectShows()
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun observeCachedShows(categoryId: Long): Flow<List<SelectShowsByCategory>> {
        return database.showQueries.selectShowsByCategory(categoryId)
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun observeShowImages(): Flow<List<SelectShowImages>> {
        return database.showQueries.selectShowImages()
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun getTvShow(traktId: Long): SelectByShowId =
        database.showQueries.selectByShowId(traktId)
            .executeAsOne()

    override fun deleteTvShows() {
        database.showQueries.deleteAll()
    }

    override fun getShowsByCategoryID(categoryId: Long): List<SelectShowsByCategory> =
        database.showQueries.selectShowsByCategory(categoryId)
            .executeAsList()
}
