package com.thomaskioko.tvmaniac.shows.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import kotlinx.coroutines.flow.Flow

class TvShowCacheImpl(
    private val database: TvManiacDatabase
) : TvShowCache {

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

    override fun observeTvShow(showId: Int): Flow<SelectByShowId?> {
        return database.showQueries.selectByShowId(showId)
            .asFlow()
            .mapToOneOrNull()
    }

    override fun observeTvShows(): Flow<List<SelectShows>> {
        return database.showQueries.selectShows()
            .asFlow()
            .mapToList()
    }

    override fun observeCachedShows(categoryId: Int): Flow<List<SelectShowsByCategory>> {
        return database.showQueries.selectShowsByCategory(categoryId)
            .asFlow()
            .mapToList()
    }

    override fun getTvShow(traktId: Int): SelectByShowId? =
        database.showQueries.selectByShowId(traktId)
            .executeAsOneOrNull()

    override fun getTvShowByTmdbId(tmdbId: Int?): Show? =
        database.showQueries.selectShowByTmdbId(tmdbId)
            .executeAsOneOrNull()



    override fun deleteTvShows() {
        database.showQueries.deleteAll()
    }

    override fun getShowsByCategoryID(categoryId: Int): List<SelectShowsByCategory> =
        database.showQueries.selectShowsByCategory(categoryId)
            .executeAsList()
}
