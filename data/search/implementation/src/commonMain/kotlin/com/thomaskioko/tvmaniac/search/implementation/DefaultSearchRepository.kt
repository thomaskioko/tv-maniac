package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEARCH_RESULTS
import com.thomaskioko.tvmaniac.search.api.SearchDao
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSearchRepository(
    private val searchDao: SearchDao,
    private val store: SearchShowStore,
    private val accountManager: AccountManager,
    private val requestManagerRepository: RequestManagerRepository,
    private val dateTimeProvider: DateTimeProvider,
) : SearchRepository {

    override suspend fun search(query: String, forceRefresh: Boolean) {
        val key = searchKey(query)
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = key.cacheKey.hashCode().toLong(),
            requestType = SEARCH_RESULTS.name,
            threshold = SEARCH_RESULTS.duration,
        )
        when {
            forceRefresh || isExpired -> store.fresh(key)
            else -> store.get(key)
        }
    }

    override fun observeSearchResults(query: String): Flow<List<ShowEntity>> =
        searchDao.observeResults(searchKey(query).cacheKey)

    override fun observeRecentSearches(): Flow<List<String>> = searchDao.observeRecentSearches()

    override suspend fun saveRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        searchDao.upsertRecentSearch(query = trimmed, searchedAt = dateTimeProvider.nowMillis())
    }

    override suspend fun removeRecentSearch(query: String) {
        searchDao.deleteRecentSearch(query)
    }

    override suspend fun clearRecentSearches() {
        searchDao.deleteAllRecentSearches()
    }

    private fun searchKey(query: String): SearchKey = SearchKey(
        provider = accountManager.getActiveProvider() ?: SyncProviderSource.TRAKT,
        query = query.trim(),
    )
}
