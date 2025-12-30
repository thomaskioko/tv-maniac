package com.thomaskioko.tvmaniac.data.featuredshows.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public const val DEFAULT_API_PAGE: Long = 1

public interface FeaturedShowsRepository {
    public suspend fun fetchFeaturedShows(
        forceRefresh: Boolean,
    )

    public fun observeFeaturedShows(
        page: Long = DEFAULT_API_PAGE,
    ): Flow<List<ShowEntity>>
}
