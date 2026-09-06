package com.thomaskioko.tvmaniac.lists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.lists.api.ListDao
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import com.thomaskioko.tvmaniac.lists.api.ListShowDao
import com.thomaskioko.tvmaniac.lists.api.UserList
import com.thomaskioko.tvmaniac.lists.api.UserListEntity
import com.thomaskioko.tvmaniac.shows.api.ShowTraktIdResolver
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultListRepository(
    private val traktListsStore: TraktListsStore,
    private val traktListItemsStore: TraktListItemsStore,
    private val listDao: ListDao,
    private val listShowDao: ListShowDao,
    private val traktListRemoteDataSource: TraktListRemoteDataSource,
    private val tvShowsDao: TvShowsDao,
    private val traktIdResolver: ShowTraktIdResolver,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : ListRepository {

    override fun observeLists(): Flow<List<UserListEntity>> =
        listDao.observeListsWithPosters().distinctUntilChanged()

    override fun observeListsForShow(showId: Long): Flow<List<UserList>> =
        combine(
            listDao.observeAll(),
            listShowDao.observeByShowId(showId),
            listShowDao.observeActiveCountByListId(),
        ) { lists, showEntries, activeCounts ->
            val activeEntryListIds = showEntries
                .filter { it.pendingAction != PendingAction.DELETE.value }
                .map { it.listId }
                .toSet()
            lists.map { list ->
                UserList(
                    id = list.id,
                    slug = list.slug,
                    name = list.name,
                    description = list.description,
                    itemCount = activeCounts[list.id] ?: 0L,
                    isShowInList = list.id in activeEntryListIds,
                )
            }
        }.distinctUntilChanged()

    override suspend fun fetchUserLists(slug: String, forceRefresh: Boolean) {
        fetchListMetadata(slug = slug, forceRefresh = forceRefresh)
        fetchListItems(slug = slug, forceRefresh = forceRefresh)
    }

    private suspend fun fetchListMetadata(slug: String, forceRefresh: Boolean) {
        if (forceRefresh) {
            traktListsStore.fresh(key = slug)
        } else {
            traktListsStore.get(key = slug)
        }
    }

    private suspend fun fetchListItems(slug: String, forceRefresh: Boolean) {
        val listIds = listDao.observeAll().first().map { it.id }
        listIds.forEach { listId ->
            val key = TraktListItemsKey(userSlug = slug, listId = listId)
            if (forceRefresh) {
                traktListItemsStore.fresh(key = key)
            } else {
                traktListItemsStore.get(key = key)
            }
        }
    }

    override suspend fun createList(name: String, traktSlug: String?) {
        withContext(dispatchers.io) {
            val localId = listDao.insertLocal(name = name, createdAt = dateTimeProvider.now().toString())
            if (traktSlug == null) return@withContext

            when (val response = traktListRemoteDataSource.createList(userSlug = traktSlug, name = name)) {
                is ApiResponse.Success -> listDao.markSynced(
                    id = localId,
                    traktId = response.body.ids.trakt.toLong(),
                    slug = response.body.ids.slug,
                )
                else -> Unit
            }
        }
    }

    override suspend fun toggleShowInList(listId: Long, showId: Long, isCurrentlyInList: Boolean, traktSlug: String?) {
        withContext(dispatchers.io) {
            if (isCurrentlyInList) {
                listShowDao.updatePendingAction(
                    listId = listId,
                    tmdbId = showId,
                    pendingAction = PendingAction.DELETE.value,
                )
            } else {
                listShowDao.upsert(
                    listId = listId,
                    tmdbId = showId,
                    listedAt = "",
                    pendingAction = PendingAction.UPLOAD.value,
                )
            }

            if (traktSlug == null) return@withContext
            val listTraktId = listDao.getTraktId(listId) ?: return@withContext

            traktIdResolver.resolveMissingTraktIds(listOf(showId))
            val showTraktId = tvShowsDao.getTraktIdByTmdbId(showId) ?: return@withContext

            val response = if (isCurrentlyInList) {
                traktListRemoteDataSource.removeShowFromList(traktSlug, listTraktId, showTraktId)
            } else {
                traktListRemoteDataSource.addShowToList(traktSlug, listTraktId, showTraktId)
            }

            when (response) {
                is ApiResponse.Success -> {
                    if (isCurrentlyInList) {
                        listShowDao.deleteByListIdAndTmdbId(listId = listId, tmdbId = showId)
                    } else {
                        listShowDao.updatePendingAction(
                            listId = listId,
                            tmdbId = showId,
                            pendingAction = PendingAction.NOTHING.value,
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    override suspend fun countPendingListShows(): Long = listShowDao.countPendingActions()
}
