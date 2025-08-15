package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultFeaturedShowsRepository(
    private val store: FeaturedShowsStore,
    private val dao: FeaturedShowsDao,
) : FeaturedShowsRepository {

    companion object {
        private const val FEATURED_SHOWS_COUNT = 12
    }

    override fun observeFeaturedShows(page: Long): Flow<List<ShowEntity>> = dao.observeFeaturedShows(page)
        .map { it.take(FEATURED_SHOWS_COUNT) }

    override suspend fun fetchFeaturedShows(forceRefresh: Boolean) {
        // TODO:: Get the page from the dao
        val page = 1L
        when {
            forceRefresh -> store.fresh(page)
            else -> store.get(page)
        }
    }
}
