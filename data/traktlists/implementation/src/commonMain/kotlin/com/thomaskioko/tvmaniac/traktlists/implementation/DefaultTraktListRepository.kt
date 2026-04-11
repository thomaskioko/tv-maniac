package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.traktlists.api.TraktList
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import com.thomaskioko.tvmaniac.traktlists.api.TraktListShowDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktListRepository(
    private val traktListsStore: TraktListsStore,
    private val createTraktListStore: CreateTraktListStore,
    private val traktListDao: TraktListDao,
    private val traktListShowDao: TraktListShowDao,
    private val traktListRemoteDataSource: TraktListRemoteDataSource,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktListRepository {

    override fun observeLists(): Flow<List<TraktListEntity>> =
        traktListDao.observeAll().distinctUntilChanged()

    override fun observeListsForShow(traktShowId: Long): Flow<List<TraktList>> =
        combine(
            traktListDao.observeAll(),
            traktListShowDao.observeByShowTraktId(traktShowId),
        ) { lists, showEntries ->
            val activeEntryListIds = showEntries
                .filter { it.pendingAction != PendingAction.DELETE.value }
                .map { it.listId }
                .toSet()
            lists.map { list ->
                TraktList(
                    id = list.id,
                    slug = list.slug,
                    name = list.name,
                    description = list.description,
                    itemCount = list.itemCount,
                    isShowInList = list.id in activeEntryListIds,
                )
            }
        }.distinctUntilChanged()

    override suspend fun fetchUserLists(slug: String, forceRefresh: Boolean) {
       /* if (forceRefresh) {
            traktListsStore.fresh(key = slug)
        } else {
            traktListsStore.get(key = slug)
        }*/
        traktListsStore.fresh(key = slug)
    }

    override suspend fun createList(slug: String, name: String) {
        createTraktListStore.fresh(key = CreateTraktListParams(slug = slug, name = name))
    }

    override suspend fun toggleShowInList(slug: String, listId: Long, traktShowId: Long, isCurrentlyInList: Boolean) {
        withContext(dispatchers.io) {
            if (isCurrentlyInList) {
                traktListShowDao.updatePendingAction(
                    listId = listId,
                    showTraktId = traktShowId,
                    pendingAction = PendingAction.DELETE.value,
                )
                when (traktListRemoteDataSource.removeShowFromList(slug, listId, traktShowId)) {
                    is ApiResponse.Success -> {
                        traktListShowDao.deleteByListIdAndShowId(
                            listId = listId,
                            showTraktId = traktShowId,
                        )
                    }
                    else -> {
                        traktListShowDao.updatePendingAction(
                            listId = listId,
                            showTraktId = traktShowId,
                            pendingAction = PendingAction.NOTHING.value,
                        )
                    }
                }
            } else {
                traktListShowDao.upsert(
                    listId = listId,
                    showTraktId = traktShowId,
                    listedAt = "",
                    pendingAction = PendingAction.UPLOAD.value,
                )
                when (traktListRemoteDataSource.addShowToList(slug, listId, traktShowId)) {
                    is ApiResponse.Success -> {
                        traktListShowDao.updatePendingAction(
                            listId = listId,
                            showTraktId = traktShowId,
                            pendingAction = PendingAction.NOTHING.value,
                        )
                    }
                    else -> {
                        traktListShowDao.deleteByListIdAndShowId(
                            listId = listId,
                            showTraktId = traktShowId,
                        )
                    }
                }
            }
        }
    }
}
