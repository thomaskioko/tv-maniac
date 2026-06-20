package com.thomaskioko.tvmaniac.genre

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.genre.model.TraktGenreEntity
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktGenreDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktGenreDao {
    private val genreQueries = database.traktGenresQueries
    private val genreShowsQueries = database.genreShowsQueries

    override fun upsertGenre(slug: String, name: String) {
        genreQueries.upsert(slug = slug, name = name)
    }

    override fun observeGenres(): Flow<List<TraktGenreEntity>> =
        genreQueries.allGenres { slug, name ->
            TraktGenreEntity(slug = slug, name = name)
        }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun getGenreSlugs(): List<String> =
        genreQueries.allSlugs().executeAsList()

    override fun deleteAllGenres() {
        genreQueries.deleteAll()
    }

    override fun upsertGenreShow(genreSlug: String, showId: Long, pageOrder: Long, category: String) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        genreShowsQueries.upsert(
            genre_slug = genreSlug,
            show_id = internalShowId,
            page_order = pageOrder,
            category = category,
        )
    }

    override fun observeShowsByGenreSlug(slug: String): Flow<List<ShowEntity>> =
        genreShowsQueries.showsByGenreSlug(slug) { showId, tmdbId, name, posterPath, overview, status, ratings, year, _ ->
            ShowEntity(
                showId = showId.id,
                tmdbId = tmdbId.id,
                title = name,
                posterPath = posterPath,
                overview = overview,
                status = status,
                voteAverage = ratings,
                year = year,
                inLibrary = false,
            )
        }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeShowsByGenreSlugAndCategory(slug: String, category: String): Flow<List<ShowEntity>> =
        genreShowsQueries.showsByGenreSlugAndCategory(slug, category) { showId, tmdbId, name, posterPath, overview, status, ratings, year, _ ->
            ShowEntity(
                showId = showId.id,
                tmdbId = tmdbId.id,
                title = name,
                posterPath = posterPath,
                overview = overview,
                status = status,
                voteAverage = ratings,
                year = year,
                inLibrary = false,
            )
        }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeGenresWithShowsByCategory(category: String): Flow<List<GenreWithShowsEntity>> =
        genreShowsQueries.genresWithShowsByCategory(category) { genreSlug, genreName, showId, tmdbId, name, posterPath, overview, status, ratings, year, _ ->
            GenreShowRow(
                genreSlug = genreSlug,
                genreName = genreName,
                show = ShowEntity(
                    showId = showId.id,
                    tmdbId = tmdbId.id,
                    title = name,
                    posterPath = posterPath,
                    overview = overview,
                    status = status,
                    voteAverage = ratings,
                    year = year,
                    inLibrary = false,
                ),
            )
        }
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.groupBy { it.genreSlug to it.genreName }
                    .map { (key, groupedRows) ->
                        GenreWithShowsEntity(
                            genre = TraktGenreEntity(slug = key.first, name = key.second),
                            shows = groupedRows.map { it.show },
                        )
                    }
            }

    override fun deleteShowsByGenreSlugAndCategory(slug: String, category: String) {
        genreShowsQueries.deleteByGenreSlugAndCategory(slug, category)
    }

    override fun deleteShowsByGenreSlug(slug: String) {
        genreShowsQueries.deleteByGenreSlug(slug)
    }
}

private data class GenreShowRow(
    val genreSlug: String,
    val genreName: String,
    val show: ShowEntity,
)
