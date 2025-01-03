package com.thomaskioko.tvmaniac.data.featuredshows.api

import com.thomaskioko.tvmaniac.core.db.Featured_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface FeaturedShowsDao {
  fun upsert(show: Featured_shows)

  fun observeFeaturedShows(page: Long): Flow<List<ShowEntity>>

  fun deleteFeaturedShows(id: Long)

  fun deleteFeaturedShows()
}
