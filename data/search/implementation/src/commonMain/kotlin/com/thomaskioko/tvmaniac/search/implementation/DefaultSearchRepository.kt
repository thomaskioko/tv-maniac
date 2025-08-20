package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

private const val MIN_SHOW_COUNT = 10

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultSearchRepository(
    private val tvShowsDao: TvShowsDao,
    private val store: SearchShowStore,
) : SearchRepository {
    override suspend fun search(query: String) {
        val shouldFetch = hasNoLocalData(query)
        when {
            shouldFetch -> store.fresh(query)
            else -> store.get(query)
        }
    }

    override fun observeSearchResults(query: String): Flow<List<ShowEntity>> = tvShowsDao.observeShowsByQuery(query)

    private suspend fun hasNoLocalData(query: String): Boolean {
        return tvShowsDao.observeQueryCount(query).first().let { cachedShows ->
            cachedShows < MIN_SHOW_COUNT
        }
    }
}
