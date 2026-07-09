package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRAKT_LIST_ITEMS_SYNC
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktListItemResponse
import com.thomaskioko.tvmaniac.traktlists.api.TraktListShowDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

public data class TraktListItemsKey(
    val userSlug: String,
    val listId: Long,
)

@Inject
@SingleIn(AppScope::class)
public class TraktListItemsStore(
    private val traktListRemoteDataSource: TraktListRemoteDataSource,
    private val traktListShowDao: TraktListShowDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<TraktListItemsKey, TraktListItemsKey> by storeBuilder(
    fetcher = apiFetcher { key: TraktListItemsKey ->
        val response = traktListRemoteDataSource.getListItems(userSlug = key.userSlug, listId = key.listId)
        if (response is ApiResponse.Error.HttpError && response.code == 404) {
            ApiResponse.Success(emptyList())
        } else {
            response
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: TraktListItemsKey -> flowOf(key) },
        writer = { key: TraktListItemsKey, response: List<TraktListItemResponse> ->
            transactionRunner {
                traktListShowDao.deleteSyncedByListId(key.listId)
                response.forEach { item ->
                    val show = item.show
                    if (item.type == TYPE_SHOW && show != null) {
                        traktListShowDao.upsertSynced(
                            listId = key.listId,
                            traktId = show.ids.trakt,
                            listedAt = item.listedAt,
                        )
                    }
                }
            }
            requestManagerRepository.upsert(
                entityId = key.listId,
                requestType = TRAKT_LIST_ITEMS_SYNC.name,
            )
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { key: TraktListItemsKey ->
        withContext(dispatchers.io) {
            !requestManagerRepository.isRequestExpired(
                entityId = key.listId,
                requestType = TRAKT_LIST_ITEMS_SYNC.name,
                threshold = TRAKT_LIST_ITEMS_SYNC.duration,
            )
        }
    },
).build()

private const val TYPE_SHOW = "show"
