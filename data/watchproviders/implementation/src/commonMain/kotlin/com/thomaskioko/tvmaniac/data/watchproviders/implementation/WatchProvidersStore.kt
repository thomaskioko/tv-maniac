package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.WatchProvidersByShowId
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCH_PROVIDERS
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class WatchProvidersStore(
    private val remoteDataSource: TmdbShowDetailsNetworkDataSource,
    private val showIdResolver: ShowIdResolver,
    private val dao: WatchProviderDao,
    private val mapper: WatchProvidersMapper,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<WatchProvidersByShowId>> by storeBuilder(
    fetcher = Fetcher.of { tmdbShowId: Long ->
        when (val response = remoteDataSource.getShowWatchProviders(tmdbShowId)) {
            is ApiResponse.Success -> {
                requestManagerRepository.upsert(
                    entityId = tmdbShowId,
                    requestType = WATCH_PROVIDERS.name,
                )
                WatchProvidersFetchResult(tmdbShowId, response.body)
            }
            is ApiResponse.Unauthenticated -> throw Throwable("Not authenticated")
            is ApiResponse.Error.NetworkFailure -> throw Throwable("Network failure: ${response.kind}", response.cause)
            is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.OfflineError -> throw Throwable("No internet connection")
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, WatchProvidersFetchResult, List<WatchProvidersByShowId>>(
        reader = { showId ->
            dao.observeWatchProvidersByShowId(showId)
        },
        writer = { showId, result ->
            databaseTransactionRunner {
                dao.deleteByShowId(showId)
                val internalShowId = showIdResolver.showIdForTmdbId(showId)
                if (internalShowId != null) {
                    result.response.results.US
                        ?.let { mapper.mapToRows(us = it, tmdbId = result.tmdbId, showId = internalShowId) }
                        ?.forEach(dao::upsert)
                }
            }
        },
        delete = { showId ->
            databaseTransactionRunner {
                dao.deleteByShowId(showId)
            }
        },
        deleteAll = { databaseTransactionRunner(dao::deleteAll) },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { result ->
        withContext(dispatchers.io) {
            val showId = result.firstOrNull()?.show_id?.id ?: return@withContext false
            !requestManagerRepository.isRequestExpired(
                entityId = showId,
                requestType = WATCH_PROVIDERS.name,
                threshold = WATCH_PROVIDERS.duration,
            )
        }
    },
).build()

private data class WatchProvidersFetchResult(
    val tmdbId: Long,
    val response: WatchProvidersResult,
)
