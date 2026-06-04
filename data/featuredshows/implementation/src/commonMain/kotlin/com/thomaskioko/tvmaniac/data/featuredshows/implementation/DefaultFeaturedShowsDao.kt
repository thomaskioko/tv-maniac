package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFeaturedShowsDao(
    database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : FeaturedShowsDao {

    private val featuredShowsQueries = database.featuredShowsQueries

    override fun upsert(show: Featured_shows) {
        featuredShowsQueries.transaction {
            featuredShowsQueries.insert(
                showId = show.show_id,
                tmdbId = show.tmdb_id,
                name = show.name,
                poster_path = show.poster_path,
                overview = show.overview,
                page_order = show.page_order,
            )
        }
    }

    override fun observeFeaturedShows(page: Long): Flow<List<ShowEntity>> =
        featuredShowsQueries
            .entriesInPage { traktId, tmdbId, name, posterPath, overview, inLibrary ->
                ShowEntity(
                    traktId = traktId.id,
                    tmdbId = tmdbId.id,
                    title = name,
                    posterPath = posterPath,
                    overview = overview,
                    inLibrary = inLibrary == 1L,
                )
            }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun deleteFeaturedShows(id: Long) {
        val showId = showIdResolver.showIdForTraktId(id) ?: return
        featuredShowsQueries.delete(showId)
    }

    override fun deleteFeaturedShows() {
        featuredShowsQueries.transaction { featuredShowsQueries.deleteAll() }
    }
}
