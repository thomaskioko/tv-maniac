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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTvShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TvShowsDao {

    private val tvShowQueries = database.tvShowQueries
    private val genresQueries = database.showGenresQueries

    override suspend fun shouldUpdateShows(shows: List<Int>): Boolean {
        if (shows.isEmpty()) return false

        return shows.any { id ->
            !tvShowQueries.exists(Id(id.toLong()))
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
            id = show.id,
            name = show.name,
            overview = show.overview,
            language = show.language,
            first_air_date = show.first_air_date,
            vote_average = show.vote_average,
            vote_count = show.vote_count,
            popularity = show.popularity,
            genre_ids = show.genre_ids,
            status = show.status,
            episode_numbers = show.episode_numbers,
            last_air_date = show.last_air_date,
            season_numbers = show.season_numbers,
            poster_path = show.poster_path,
            backdrop_path = show.backdrop_path,
        )

        show.genre_ids.forEach { genreId ->
            if (genresQueries.exists(Id(genreId.toLong())).executeAsOne()) {
                genresQueries.upsert(
                    show_id = show.id,
                    genre_id = Id(genreId.toLong()),
                )
            }
        }
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
            ) { id, title, imageUrl, overview, status, voteAverage, year, inLibrary ->
                ShowEntity(
                    id = id.id,
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

    override fun getShowById(id: Long): Tvshow? {
        return tvShowQueries.transactionWithResult {
            if (!tvShowQueries.exists(Id(id)).executeAsOne()) {
                return@transactionWithResult null
            }

            tvShowQueries.tvshowDetails(Id(id)) { showId, name, overview, language, first_air_date,
                                                  last_air_date, popularity, vote_average, status, vote_count, poster_path,
                                                  backdrop_path, genre_list, in_library,
                ->

                val genreIds = genre_list?.split(", ")?.mapNotNull { it.toIntOrNull() } ?: emptyList()

                Tvshow(
                    id = showId,
                    name = name,
                    overview = overview,
                    language = language,
                    first_air_date = first_air_date,
                    last_air_date = last_air_date,
                    popularity = popularity,
                    vote_average = vote_average,
                    status = status,
                    vote_count = vote_count,
                    poster_path = poster_path,
                    backdrop_path = backdrop_path,
                    genre_ids = genreIds,
                    episode_numbers = null, // Not available in tvshowDetails query
                    season_numbers = null, // Not available in tvshowDetails query
                )
            }.executeAsOneOrNull()
        }
    }

    override fun showExists(id: Long): Boolean {
        return tvShowQueries.exists(Id(id)).executeAsOne()
    }

    override fun getShowsByIds(ids: List<Long>): List<ShowEntity> {
        if (ids.isEmpty()) return emptyList()

        return tvShowQueries.showsByIdsStable(ids.map(::Id)) { id, name, posterPath, overview, inLibrary ->
            ShowEntity(
                id = id.id,
                title = name,
                posterPath = posterPath,
                overview = overview,
                inLibrary = inLibrary == 1L,
            )
        }.executeAsList()
    }
}
