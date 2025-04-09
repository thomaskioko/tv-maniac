package com.thomaskioko.tvmaniac.data.featuredshows.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

const val DEFAULT_API_PAGE: Long = 1

interface FeaturedShowsRepository {
  suspend fun fetchFeaturedShows(
    forceRefresh: Boolean,
  )

  fun observeFeaturedShows(
    page: Long = DEFAULT_API_PAGE,
  ): Flow<List<ShowEntity>>
}
