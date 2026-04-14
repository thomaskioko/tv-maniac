package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRAKT_LISTS_SYNC
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
@SingleIn(AppScope::class)
public class TraktListsStore(
    private val traktListDataSource: TraktListRemoteDataSource,
    private val traktListDao: TraktListDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, List<TraktListEntity>> by storeBuilder(
    fetcher = apiFetcher { userId: String ->
        traktListDataSource.getUserList(userId)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: String -> traktListDao.observeAll() },
        writer = { _: String, response: List<TraktPersonalListsResponse> ->
            transactionRunner {
                traktListDao.deleteAll()
                response.forEach { listResponse ->
                    traktListDao.upsert(listResponse.toEntity())
                }
            }

            requestManagerRepository.upsert(
                entityId = TRAKT_LISTS_SYNC.requestId,
                requestType = TRAKT_LISTS_SYNC.name,
            )
        },
        delete = { _: String -> traktListDao.deleteAll() },
        deleteAll = { traktListDao.deleteAll() },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = TRAKT_LISTS_SYNC.name,
                threshold = TRAKT_LISTS_SYNC.duration,
            )
        }
    },
).build()

private fun TraktPersonalListsResponse.toEntity(): TraktListEntity = TraktListEntity(
    id = ids.trakt.toLong(),
    slug = ids.slug,
    name = name,
    description = description,
    itemCount = item_count.toLong(),
    createdAt = createdAt,
)
