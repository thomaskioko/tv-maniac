package com.thomaskioko.tvmaniac.shows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTvShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TvShowsDao {

    private val tvShowQueries = database.tvShowQueries

    override suspend fun shouldUpdateShows(shows: List<Int>): Boolean {
        if (shows.isEmpty()) return false

        return shows.any { traktId ->
            !tvShowQueries.exists(Id(traktId.toLong()))
                .executeAsOne()
        }
    }

    override fun upsert(show: Tvshow) {
        tvShowQueries.transaction {
            upsertShowWithGenres(show)
        }
    }

    override fun upsert(list: List<Tvshow>) {
        if (list.isEmpty()) return

        tvShowQueries.transaction {
            list.forEach { show ->
                upsertShowWithGenres(show)
            }
        }
    }

    private fun upsertShowWithGenres(show: Tvshow) {
        tvShowQueries.upsert(
            trakt_id = show.trakt_id,
            tmdb_id = show.tmdb_id,
            name = show.name,
            overview = show.overview,
            language = show.language,
            year = show.year,
            ratings = show.ratings,
            vote_count = show.vote_count,
            genres = show.genres,
            status = show.status,
            episode_numbers = show.episode_numbers,
            season_numbers = show.season_numbers,
            poster_path = show.poster_path,
            backdrop_path = show.backdrop_path,
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
                    traktId = traktId.id,
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

    override fun deleteTvShows() {
        tvShowQueries.transaction { tvShowQueries.deleteAll() }
    }

    override fun getShowByTraktId(traktId: Long): Tvshow? {
        return tvShowQueries.transactionWithResult {
            if (!tvShowQueries.exists(Id(traktId)).executeAsOne()) {
                return@transactionWithResult null
            }

            tvShowQueries.tvshowDetails(Id(traktId)) { showTraktId, showTmdbId, name, overview, language, year,
                                                       ratings, status, voteCount, posterPath,
                                                       backdropPath, genres, seasonNumbers, _,
                ->
                Tvshow(
                    trakt_id = showTraktId,
                    tmdb_id = showTmdbId,
                    name = name,
                    overview = overview,
                    language = language,
                    year = year,
                    ratings = ratings,
                    status = status,
                    vote_count = voteCount,
                    poster_path = posterPath,
                    backdrop_path = backdropPath,
                    genres = genres,
                    episode_numbers = null,
                    season_numbers = seasonNumbers,
                )
            }.executeAsOneOrNull()
        }
    }

    override fun showExistsByTraktId(traktId: Long): Boolean {
        return tvShowQueries.exists(Id(traktId)).executeAsOne()
    }

    override fun getShowsByTraktIds(traktIds: List<Long>): List<ShowEntity> {
        if (traktIds.isEmpty()) return emptyList()

        return tvShowQueries.showsByTraktIds(traktIds.map(::Id)) { traktId, tmdbId, name, posterPath, overview, inLibrary ->
            ShowEntity(
                traktId = traktId.id,
                tmdbId = tmdbId.id,
                title = name,
                posterPath = posterPath,
                overview = overview,
                inLibrary = inLibrary == 1L,
            )
        }.executeAsList()
    }

    override fun getTmdbIdByTraktId(traktId: Long): Long? {
        return tvShowQueries.getTmdbIdByTraktId(Id(traktId)).executeAsOneOrNull()?.id
    }
}
