package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFeaturedShowsRepository(
    private val store: FeaturedShowsStore,
    private val dao: FeaturedShowsDao,
) : FeaturedShowsRepository {

    public companion object {
        private const val FEATURED_SHOWS_COUNT = 12
    }

    override fun observeFeaturedShows(page: Long): Flow<List<ShowEntity>> = dao.observeFeaturedShows(page)
        .map { it.take(FEATURED_SHOWS_COUNT) }

    override suspend fun fetchFeaturedShows(forceRefresh: Boolean, page: Long) {
        when {
            forceRefresh -> store.fresh(page)
            else -> store.get(page)
        }
    }
}
