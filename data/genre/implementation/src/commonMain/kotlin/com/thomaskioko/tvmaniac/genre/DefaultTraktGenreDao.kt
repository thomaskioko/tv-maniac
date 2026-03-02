package com.thomaskioko.tvmaniac.genre

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.genre.model.TraktGenreEntity
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktGenreDao(
    private val database: TvManiacDatabase,
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

    override fun upsertGenreShow(genreSlug: String, traktId: Long, pageOrder: Long, category: String) {
        genreShowsQueries.upsert(
            genre_slug = genreSlug,
            trakt_id = traktId,
            page_order = pageOrder,
            category = category,
        )
    }

    override fun observeShowsByGenreSlug(slug: String): Flow<List<ShowEntity>> =
        genreShowsQueries.showsByGenreSlug(slug) { traktId, tmdbId, name, posterPath, overview, status, ratings, year, _ ->
            ShowEntity(
                traktId = traktId.id,
                tmdbId = tmdbId.id,
                title = name,
                posterPath = posterPath,
                overview = overview ?: "",
                status = status,
                voteAverage = ratings,
                year = year,
                inLibrary = false,
            )
        }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeShowsByGenreSlugAndCategory(slug: String, category: String): Flow<List<ShowEntity>> =
        genreShowsQueries.showsByGenreSlugAndCategory(slug, category) { traktId, tmdbId, name, posterPath, overview, status, ratings, year, _ ->
            ShowEntity(
                traktId = traktId.id,
                tmdbId = tmdbId.id,
                title = name,
                posterPath = posterPath,
                overview = overview ?: "",
                status = status,
                voteAverage = ratings,
                year = year,
                inLibrary = false,
            )
        }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeGenresWithShowsByCategory(category: String): Flow<List<GenreWithShowsEntity>> =
        genreShowsQueries.genresWithShowsByCategory(category) { genreSlug, genreName, traktId, tmdbId, name, posterPath, overview, status, ratings, year, _ ->
            GenreShowRow(
                genreSlug = genreSlug,
                genreName = genreName,
                show = ShowEntity(
                    traktId = traktId.id,
                    tmdbId = tmdbId.id,
                    title = name,
                    posterPath = posterPath,
                    overview = overview ?: "",
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
