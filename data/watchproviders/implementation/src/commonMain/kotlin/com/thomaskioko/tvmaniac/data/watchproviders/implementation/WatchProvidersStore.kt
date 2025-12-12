package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.Watch_providers
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCH_PROVIDERS
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
class WatchProvidersStore(
    private val remoteDataSource: TmdbShowDetailsNetworkDataSource,
    private val dao: WatchProviderDao,
    private val formatterUtil: FormatterUtil,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<WatchProviders>> by storeBuilder(
    fetcher = Fetcher.of { id ->
        when (val response = remoteDataSource.getShowWatchProviders(id)) {
            is ApiResponse.Success -> response.body
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable("${response.errorMessage}")
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, WatchProvidersResult, List<WatchProviders>>(
        reader = { id -> dao.observeWatchProviders(id) },
        writer = { id, response ->
            databaseTransactionRunner {
                // TODO:: Get users locale and format the date accordingly.
                response.results.US?.let { usProvider ->
                    usProvider.free.forEach {
                        dao.upsert(
                            Watch_providers(
                                id = Id(it.providerId.toLong()),
                                logo_path = it.logoPath?.let { path -> formatterUtil.formatTmdbPosterPath(path) },
                                name = it.providerName,
                                tmdb_id = Id(id),
                            ),
                        )
                    }
                    usProvider.flatrate.forEach {
                        dao.upsert(
                            Watch_providers(
                                id = Id(it.providerId.toLong()),
                                logo_path = it.logoPath?.let { path -> formatterUtil.formatTmdbPosterPath(path) },
                                name = it.providerName,
                                tmdb_id = Id(id),
                            ),
                        )
                    }
                }

                requestManagerRepository.upsert(
                    entityId = id,
                    requestType = WATCH_PROVIDERS.name,
                )
            }
        },
        delete = { databaseTransactionRunner { dao.delete(it) } },
        deleteAll = { databaseTransactionRunner(dao::deleteAll) },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { cachedData ->
        withContext(dispatchers.io) {
            val showId = cachedData.firstOrNull()?.tmdb_id?.id ?: return@withContext false
            !requestManagerRepository.isRequestExpired(
                entityId = showId,
                requestType = WATCH_PROVIDERS.name,
                threshold = WATCH_PROVIDERS.duration,
            )
        }
    },
).build()
