package com.thomaskioko.tvmaniac.details.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
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
                poster_image_url = show.poster_image_url,
                backdrop_image_url = show.backdrop_image_url,
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

    override fun updateShow(
        tmdbId: Int,
        posterUrl: String?,
        backdropUrl: String?
    ) {
        database.transaction {
            database.showQueries.updateTvShow(
                tmdb_id = tmdbId,
                poster_image_url = posterUrl,
                backdrop_image_url = backdropUrl
            )
        }
    }

    override fun observeTvShow(showId: Int): Flow<Show?> {
        return database.showQueries.selectByShowId(showId)
            .asFlow()
            .mapToOneOrNull()
    }

    override fun observeTvShows(): Flow<List<Show>> {
        return database.showQueries.selectAll()
            .asFlow()
            .mapToList()
    }

    override fun observeShowAirEpisodes(showId: Int): Flow<List<AirEpisodesByShowId>> {
        return database.lastAirEpisodeQueries.airEpisodesByShowId(
            show_id = showId
        ).asFlow()
            .mapToList()
    }

    override fun observeShowsByCategoryID(categoryId: Int): Flow<List<SelectShowsByCategory>> {
        return database.showQueries.selectShowsByCategory(categoryId)
            .asFlow()
            .mapToList()
    }

    override fun getTvShow(traktId: Int): Show? =
        database.showQueries.selectByShowId(traktId)
            .executeAsOneOrNull()

    override fun getTvShowByTmdbId(tmdbId: Int?): Show? =
        database.showQueries.selectShowByTmdbId(tmdbId)
            .executeAsOneOrNull()

    override fun observeTvShowsArt(): Flow<List<Show>> =
        database.showQueries.selectShows()
            .asFlow()
            .mapToList()

    override fun deleteTvShows() {
        database.showQueries.deleteAll()
    }

    override fun getShowsByCategoryID(categoryId: Int): List<SelectShowsByCategory> =
        database.showQueries.selectShowsByCategory(categoryId)
            .executeAsList()
}
