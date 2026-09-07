package com.thomaskioko.tvmaniac.lists.implementation

import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig.pagingConfig
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.lists.api.ListDao
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import com.thomaskioko.tvmaniac.lists.api.ListShowDao
import com.thomaskioko.tvmaniac.lists.api.ListShowItem
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val syncPendingListsMutex = Mutex()

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
        withLocalChangesLock {
            val localId = listDao.insertLocal(name = name, createdAt = dateTimeProvider.now().toString())
            if (traktSlug == null) return@withLocalChangesLock

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

    override suspend fun renameList(listId: Long, name: String, traktSlug: String?) {
        withLocalChangesLock {
            listDao.rename(id = listId, name = name)
            val traktId = listDao.getTraktId(listId)
            if (traktSlug == null || traktId == null) return@withLocalChangesLock

            when (traktListRemoteDataSource.updateList(userSlug = traktSlug, listId = traktId, name = name)) {
                is ApiResponse.Success -> listDao.clearPendingAction(listId)
                else -> Unit
            }
        }
    }

    override suspend fun deleteList(listId: Long, traktSlug: String?) {
        withLocalChangesLock {
            val traktId = listDao.getTraktId(listId)
            if (traktId == null) {
                listShowDao.deleteByListId(listId)
                listDao.deleteById(listId)
                return@withLocalChangesLock
            }

            listDao.markPendingDelete(listId)
            if (traktSlug == null) return@withLocalChangesLock

            when (val response = traktListRemoteDataSource.deleteList(userSlug = traktSlug, listId = traktId)) {
                is ApiResponse.Success -> {
                    listShowDao.deleteByListId(listId)
                    listDao.deleteById(listId)
                }
                is ApiResponse.Error.HttpError -> if (response.code == HTTP_NOT_FOUND) {
                    listShowDao.deleteByListId(listId)
                    listDao.deleteById(listId)
                }
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

    override suspend fun syncPendingLists(slug: String) {
        withContext(dispatchers.io) {
            syncPendingListsMutex.withLock {
                if (!pushPendingListDeletes(slug)) return@withLock
                if (!pushPendingListCreations(slug)) return@withLock
                if (!pushPendingListRenames(slug)) return@withLock
                pushPendingListItems(slug)
            }
        }
    }

    private suspend fun <T> withLocalChangesLock(block: suspend () -> T): T =
        withContext(dispatchers.io) {
            syncPendingListsMutex.withLock { block() }
        }

    private suspend fun pushPendingListDeletes(slug: String): Boolean {
        val pendingDeletes = listDao.selectPendingDeletes()
        for (pending in pendingDeletes) {
            val traktId = pending.traktId
            if (traktId == null) {
                listShowDao.deleteByListId(pending.id)
                listDao.deleteById(pending.id)
                continue
            }
            when (val response = traktListRemoteDataSource.deleteList(userSlug = slug, listId = traktId)) {
                is ApiResponse.Success -> {
                    listShowDao.deleteByListId(pending.id)
                    listDao.deleteById(pending.id)
                }
                is ApiResponse.Error.HttpError -> if (response.code == HTTP_NOT_FOUND) {
                    listShowDao.deleteByListId(pending.id)
                    listDao.deleteById(pending.id)
                } else {
                    return false
                }
                else -> return false
            }
        }
        return true
    }

    private suspend fun pushPendingListRenames(slug: String): Boolean {
        val pendingRenames = listDao.selectPendingRenames()
        for (pending in pendingRenames) {
            when (traktListRemoteDataSource.updateList(userSlug = slug, listId = pending.traktId, name = pending.name)) {
                is ApiResponse.Success -> listDao.clearPendingAction(pending.id)
                else -> return false
            }
        }
        return true
    }

    private suspend fun pushPendingListCreations(slug: String): Boolean {
        val pendingLists = listDao.selectPendingUploadLists()
        if (pendingLists.isEmpty()) return true

        val remoteLists = when (val response = traktListRemoteDataSource.getUserList(slug)) {
            is ApiResponse.Success -> response.body
            else -> return false
        }.toMutableList()

        for (pending in pendingLists) {
            val match = remoteLists.firstOrNull { it.name.trim() == pending.name.trim() }
            if (match != null) {
                remoteLists.remove(match)
                listDao.markSynced(id = pending.id, traktId = match.ids.trakt.toLong(), slug = match.ids.slug)
                continue
            }

            when (val response = traktListRemoteDataSource.createList(userSlug = slug, name = pending.name)) {
                is ApiResponse.Success -> listDao.markSynced(
                    id = pending.id,
                    traktId = response.body.ids.trakt.toLong(),
                    slug = response.body.ids.slug,
                )
                else -> return false
            }
        }
        return true
    }

    private suspend fun pushPendingListItems(slug: String) {
        val pending = listShowDao.selectPendingForSyncedLists()
        if (pending.isEmpty()) return

        traktIdResolver.resolveMissingTraktIds(pending.map { it.tmdbId }.distinct())

        for (entry in pending) {
            val listTraktId = listDao.getTraktId(entry.listId) ?: continue
            val showTraktId = tvShowsDao.getTraktIdByTmdbId(entry.tmdbId) ?: continue

            val response = if (entry.pendingAction == PendingAction.DELETE.value) {
                traktListRemoteDataSource.removeShowFromList(slug, listTraktId, showTraktId)
            } else {
                traktListRemoteDataSource.addShowToList(slug, listTraktId, showTraktId)
            }

            when (response) {
                is ApiResponse.Success -> {
                    if (entry.pendingAction == PendingAction.DELETE.value) {
                        listShowDao.deleteByListIdAndTmdbId(listId = entry.listId, tmdbId = entry.tmdbId)
                    } else {
                        listShowDao.updatePendingAction(
                            listId = entry.listId,
                            tmdbId = entry.tmdbId,
                            pendingAction = PendingAction.NOTHING.value,
                        )
                    }
                }
                else -> return
            }
        }
    }

    override suspend fun countPendingListShows(): Long = listShowDao.countPendingActions()

    override suspend fun countPendingLists(): Long = listDao.countPendingChanges()

    override fun observePagedListShows(listId: Long): Flow<PagingData<ListShowItem>> =
        Pager(
            config = pagingConfig,
            pagingSourceFactory = { listShowDao.getPagedShows(listId) },
        ).flow

    override suspend fun getTmdbIdsMissingPoster(listId: Long): List<Long> =
        listShowDao.getTmdbIdsMissingPoster(listId)

    private companion object {
        private const val HTTP_NOT_FOUND = 404
    }
}
