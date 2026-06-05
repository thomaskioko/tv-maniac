package com.thomaskioko.tvmaniac.shows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.mergeShows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTvShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TvShowsDao {

    private val tvShowQueries = database.tvShowQueries
    private val externalIdQueries = database.tvshowExternalIdQueries

    override fun upsert(show: ShowToPersist) {
        tvShowQueries.transaction {
            upsertShowWithGenres(show)
        }
    }

    override fun upsert(list: List<ShowToPersist>) {
        if (list.isEmpty()) return

        tvShowQueries.transaction {
            list.forEach { show ->
                upsertShowWithGenres(show)
            }
        }
    }

    private fun upsertShowWithGenres(show: ShowToPersist) {
        tvShowQueries.upsert(
            tmdb_id = show.tmdbId,
            name = show.name,
            overview = show.overview,
            language = show.language,
            year = show.year,
            ratings = show.ratings,
            vote_count = show.voteCount,
            genres = show.genres,
            status = show.status,
            episode_numbers = show.episodeNumbers,
            season_numbers = show.seasonNumbers,
            poster_path = show.posterPath,
            backdrop_path = show.backdropPath,
        )
        val showId = tvShowQueries.getShowIdByTmdbId(show.tmdbId).executeAsOne()
        externalIdQueries.insert(
            showId = showId,
            provider = Provider.TRAKT,
            externalId = show.traktId.id.toString(),
        )
    }

    override fun observeShowsByQuery(query: String): Flow<List<ShowEntity>> {
        return tvShowQueries
            .searchShows(
                // Parameters for WHERE clause
                query,
                query,
                query,
                query,
                // Parameters for ORDER BY clause
                query,
                query,
                query,
            ) { traktId, tmdbId, title, imageUrl, overview, status, voteAverage, year, inLibrary ->
                ShowEntity(
                    traktId = traktId,
                    tmdbId = tmdbId.id,
                    title = title,
                    posterPath = imageUrl,
                    inLibrary = inLibrary == 1L,
                    overview = overview,
                    status = status,
                    voteAverage = voteAverage,
                    year = year,
                )
            }
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun observeQueryCount(query: String): Flow<Long> {
        return tvShowQueries.searchShowsCount(query, query, query, query)
            .asFlow()
            .mapToOne(dispatchers.io)
    }

    override suspend fun getQueryCount(query: String): Long =
        withContext(dispatchers.io) {
            tvShowQueries.searchShowsCount(query, query, query, query).executeAsOne()
        }

    override fun deleteTvShows() {
        tvShowQueries.transaction { tvShowQueries.deleteAll() }
    }

    override fun upsertMerging(show: ShowToPersist) {
        tvShowQueries.transaction {
            val existing = tvShowQueries.tvshowByTmdbId(show.tmdbId).executeAsOneOrNull()
            upsertShowWithGenres(mergeShows(existing, show))
        }
    }

    override fun getShowsByTraktIds(traktIds: List<Long>): List<ShowEntity> {
        if (traktIds.isEmpty()) return emptyList()

        return tvShowQueries.showsByTraktIds(traktIds) { traktId, tmdbId, name, posterPath, overview, inLibrary ->
            ShowEntity(
                traktId = traktId,
                tmdbId = tmdbId.id,
                title = name,
                posterPath = posterPath,
                overview = overview,
                inLibrary = inLibrary == 1L,
            )
        }.executeAsList()
    }

    override fun getTmdbIdByTraktId(traktId: Long): Long? {
        return tvShowQueries.getTmdbIdByTraktId(traktId).executeAsOneOrNull()?.id
    }

    override suspend fun existsByTraktId(traktId: Long): Boolean =
        withContext(dispatchers.io) {
            tvShowQueries.existsByTraktId(traktId).executeAsOne()
        }
}
