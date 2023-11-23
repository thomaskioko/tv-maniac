package com.thomaskioko.tvmaniac.shows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.Shows
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ShowDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowsDao {

    override fun upsert(show: Show) {
        database.showQueries.transaction {
            database.showQueries.insertOrReplace(
                id = show.id,
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

    override fun upsert(list: List<Show>) {
        list.forEach { upsert(it) }
    }

    override fun observeTvShow(showId: Long): Flow<ShowById> {
        return database.showQueries.showById(Id(showId))
            .asFlow()
            .mapToOne(dispatchers.io)
    }

    override fun observeCachedShows(categoryId: Long): Flow<List<ShowsByCategory>> {
        return database.show_categoryQueries.showsByCategory(Id(categoryId))
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun observeShows(): Flow<List<Shows>> {
        return database.showQueries.shows()
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun getTvShow(traktId: Long): ShowById =
        database.showQueries.showById(Id(traktId))
            .executeAsOne()

    override fun deleteTvShows() {
        database.showQueries.deleteAll()
    }
}
