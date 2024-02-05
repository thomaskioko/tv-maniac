package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Featured_shows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultFeaturedShowsDao(
  database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : FeaturedShowsDao {

  private val featuredShowsQueries = database.featured_showsQueries

  override fun upsert(show: Featured_shows) {
    featuredShowsQueries.transaction {
      featuredShowsQueries.insert(
        id = show.id,
      )
    }
  }

  override fun observeFeaturedShows(): Flow<List<ShowEntity>> =
    featuredShowsQueries
      .featuredShows { id, title, posterPath, inLibrary ->
        ShowEntity(
          id = id.id,
          title = title,
          posterPath = posterPath,
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
