package com.thomaskioko.tvmaniac.data.featuredshows.api

import com.thomaskioko.tvmaniac.shows.api.model.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface FeaturedShowsRepository {
    public suspend fun fetchFeaturedShows(
        forceRefresh: Boolean,
        page: Long = DEFAULT_API_PAGE,
    )

    public fun observeFeaturedShows(
        page: Long = DEFAULT_API_PAGE,
    ): Flow<List<ShowEntity>>
}
