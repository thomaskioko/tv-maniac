package com.thomaskioko.tvmaniac.data.featuredshows.api

import com.thomaskioko.tvmaniac.core.db.FeaturedShows
import com.thomaskioko.tvmaniac.core.db.Featured_shows
import kotlinx.coroutines.flow.Flow

interface FeaturedShowsDao {
    fun upsert(show: Featured_shows)
    fun observeFeaturedShows(): Flow<List<FeaturedShows>>
    fun deleteFeaturedShows(id: Long)
    fun deleteFeaturedShows()
}
