package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultFeaturedShowsDao(
  database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : FeaturedShowsDao {

  private val featuredShowsQueries = database.featuredShowsQueries

  override fun upsert(show: Featured_shows) {
    featuredShowsQueries.transaction {
      featuredShowsQueries.insert(
        id = show.id,
      )
    }
  }

  override fun observeFeaturedShows(page: Long): Flow<List<ShowEntity>> =
    featuredShowsQueries
      .featuredShows { id, title, posterPath, overview, inLibrary ->
        ShowEntity(
          id = id.id,
          title = title,
          posterPath = posterPath,
          inLibrary = inLibrary == 1L,
          overview = overview
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
