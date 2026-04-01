package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiGenericException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import com.thomaskioko.tvmaniac.traktlists.api.TraktListShowDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListWithMembership
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
    private val traktListDao: TraktListDao,
    private val traktListShowDao: TraktListShowDao,
    private val traktListRemoteDataSource: TraktListRemoteDataSource,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktListRepository {

    override fun observeLists(): Flow<List<TraktListEntity>> =
        traktListDao.observeAll().distinctUntilChanged()

    override fun observeListsForShow(traktShowId: Long): Flow<List<TraktListWithMembership>> =
        combine(
            traktListDao.observeAll(),
            traktListShowDao.observeByShowTraktId(traktShowId),
        ) { lists, showEntries ->
            val activeEntryListIds = showEntries
                .filter { it.pendingAction != PendingAction.DELETE.value }
                .map { it.listId }
                .toSet()
            lists.map { list ->
                TraktListWithMembership(
                    id = list.id,
                    slug = list.slug,
                    name = list.name,
                    description = list.description,
                    itemCount = list.itemCount,
                    isShowInList = list.id in activeEntryListIds,
                )
            }
        }.distinctUntilChanged()

    override suspend fun syncLists(slug: String, forceRefresh: Boolean) {
        if (forceRefresh) {
            traktListsStore.fresh(key = slug)
        } else {
            traktListsStore.get(key = slug)
        }
    }

    override suspend fun createList(slug: String, name: String) {
        withContext(dispatchers.io) {
            when (val response = traktListRemoteDataSource.createList(userSlug = slug, name = name)) {
                is ApiResponse.Success -> {
                    traktListDao.upsert(
                        TraktListEntity(
                            id = response.body.ids.trakt.toLong(),
                            slug = response.body.ids.slug,
                            name = response.body.name,
                            description = response.body.description,
                            itemCount = 0,
                            createdAt = "",
                        ),
                    )
                }
                is ApiResponse.Error.HttpError -> throw Exception("HTTP ${response.code}: ${response.errorMessage}")
                is ApiResponse.Error.SerializationError -> throw Exception("Serialization error: ${response.message}")
                is ApiResponse.Error.GenericError -> throw Exception("Error: ${response.message}")
                is ApiResponse.Unauthenticated -> throw Exception("Not authenticated")
                is ApiResponse.Error.OfflineError -> throw ApiGenericException(response.errorMessage)
            }
        }
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
