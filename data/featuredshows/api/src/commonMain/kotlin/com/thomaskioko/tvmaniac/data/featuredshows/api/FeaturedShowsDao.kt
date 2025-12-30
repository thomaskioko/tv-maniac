package com.thomaskioko.tvmaniac.data.featuredshows.api

import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface FeaturedShowsDao {
    public fun upsert(show: Featured_shows)

    public fun observeFeaturedShows(page: Long): Flow<List<ShowEntity>>

    public fun deleteFeaturedShows(id: Long)

    public fun deleteFeaturedShows()
}
