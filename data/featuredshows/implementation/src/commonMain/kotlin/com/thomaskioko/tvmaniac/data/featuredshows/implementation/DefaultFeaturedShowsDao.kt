package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFeaturedShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : FeaturedShowsDao {

    private val featuredShowsQueries = database.featuredShowsQueries

    override fun upsert(show: Featured_shows) {
        featuredShowsQueries.transaction {
            featuredShowsQueries.insert(
                id = show.id,
                name = show.name,
                poster_path = show.poster_path,
                overview = show.overview,
                page_order = show.page_order,
            )
        }
    }

    override fun observeFeaturedShows(page: Long): Flow<List<ShowEntity>> =
        featuredShowsQueries
            .entriesInPage { id, name, posterPath, overview, inLibrary ->
                ShowEntity(
                    id = id.id,
                    title = name,
                    posterPath = posterPath,
                    overview = overview,
                    inLibrary = inLibrary == 1L,
                )
            }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun deleteFeaturedShows(id: Long) {
        featuredShowsQueries.delete(Id(id))
    }

    override fun deleteFeaturedShows() {
        featuredShowsQueries.transaction { featuredShowsQueries.deleteAll() }
    }
}
