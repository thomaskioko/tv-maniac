package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.Watch_providers
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

@Inject
public class WatchProvidersStore(
    private val remoteDataSource: TmdbShowDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val dao: WatchProviderDao,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<WatchProviders>> by storeBuilder(
    fetcher = Fetcher.of { traktId ->
        val tmdbId = tvShowsDao.getTmdbIdByTraktId(traktId)
            ?: throw Throwable("TMDB ID not found for Trakt ID: $traktId")
        when (val response = remoteDataSource.getShowWatchProviders(tmdbId)) {
            is ApiResponse.Success -> WatchProvidersFetchResult(tmdbId, response.body)
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable("${response.errorMessage}")
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, WatchProvidersFetchResult, List<WatchProviders>>(
        reader = { traktId ->
            val tmdbId = tvShowsDao.getTmdbIdByTraktId(traktId)
            tmdbId?.let { dao.observeWatchProviders(it) } ?: dao.observeWatchProviders(traktId)
        },
        writer = { _, result ->
            databaseTransactionRunner {
                // TODO:: Get users locale and format the date accordingly.
                result.response.results.US?.let { usProvider ->
                    usProvider.free.forEach {
                        dao.upsert(
                            Watch_providers(
                                id = Id(it.providerId.toLong()),
                                logo_path = it.logoPath?.let { path -> formatterUtil.formatTmdbPosterPath(path) },
                                name = it.providerName,
                                tmdb_id = Id(result.tmdbId),
                            ),
                        )
                    }
                    usProvider.flatrate.forEach {
                        dao.upsert(
                            Watch_providers(
                                id = Id(it.providerId.toLong()),
                                logo_path = it.logoPath?.let { path -> formatterUtil.formatTmdbPosterPath(path) },
                                name = it.providerName,
                                tmdb_id = Id(result.tmdbId),
                            ),
                        )
                    }
                }
            }
        },
        delete = { traktId ->
            databaseTransactionRunner {
                val tmdbId = tvShowsDao.getTmdbIdByTraktId(traktId)
                tmdbId?.let { dao.delete(it) }
            }
        },
        deleteAll = { databaseTransactionRunner(dao::deleteAll) },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).build()

private data class WatchProvidersFetchResult(
    val tmdbId: Long,
    val response: WatchProvidersResult,
)
