package com.thomaskioko.tvmaniac.data.featuredshows.testing

import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeFeaturedShowsRepository : FeaturedShowsRepository {

    private val shows = MutableStateFlow<List<ShowEntity>>(emptyList())

    public fun setFeaturedShows(result: List<ShowEntity>) {
        shows.value = result
    }

    override suspend fun fetchFeaturedShows(forceRefresh: Boolean) {
    }

    override fun observeFeaturedShows(page: Long): Flow<List<ShowEntity>> {
        return shows.asStateFlow()
    }
}
