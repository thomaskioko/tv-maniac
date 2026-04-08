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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
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
            traktListShowDao.observeActiveCountByListId(),
        ) { lists, showEntries, activeCounts ->
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
                    itemCount = activeCounts[list.id] ?: 0,
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
