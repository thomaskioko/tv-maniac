package com.thomaskioko.tvmaniac.shows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.Shows
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ShowDaoImpl constructor(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowsDao {

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
                genres = show.genres,
            )
        }
    }

    override fun insert(list: List<Show>) {
        list.forEach { insert(it) }
    }

    override fun observeTvShow(showId: Long): Flow<ShowById> {
        return database.showQueries.showById(showId)
            .asFlow()
            .mapToOne(dispatchers.io)
    }

    override fun observeCachedShows(categoryId: Long): Flow<List<ShowsByCategory>> {
        return database.showQueries.showsByCategory(categoryId)
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun observeShows(): Flow<List<Shows>> {
        return database.showQueries.shows()
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun getTvShow(traktId: Long): ShowById =
        database.showQueries.showById(traktId)
            .executeAsOne()

    override fun deleteTvShows() {
        database.showQueries.deleteAll()
    }

    override fun getShowsByCategoryID(categoryId: Long): List<ShowsByCategory> =
        database.showQueries.showsByCategory(categoryId)
            .executeAsList()
}
