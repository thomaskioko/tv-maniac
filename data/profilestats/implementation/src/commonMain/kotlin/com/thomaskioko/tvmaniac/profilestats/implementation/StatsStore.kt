package com.thomaskioko.tvmaniac.profilestats.implementation

import com.thomaskioko.tvmaniac.core.db.Stats
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.profilestats.api.StatsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.impl.storeBuilderFromFetcherAndSourceOfTruth

@Inject
class StatsStore(
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val statsDao: StatsDao,
    private val mapper: StatsMapper,
    private val logger: KermitLogger,
    private val scope: AppCoroutineScope,
) : Store<String, Stats> by storeBuilderFromFetcherAndSourceOfTruth<String, Stats, Stats>(
    fetcher = Fetcher.of { slug ->

        when (val response = traktRemoteDataSource.getStats(slug)) {
            is ApiResponse.Success -> mapper.toTraktStats(slug, response.body)

            is ApiResponse.Error.GenericError -> {
                logger.error("GenericError", "${response.errorMessage}")
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("HttpError", "${response.code} - ${response.errorBody?.message}")
                throw Throwable("${response.code} - ${response.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("SerializationError", "$response")
                throw Throwable("$response")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { slug -> statsDao.observeStats(slug) },
        writer = { _, stats -> statsDao.insert(stats) },
        delete = statsDao::delete,
        deleteAll = statsDao::deleteAll,
    ),
)
    .scope(scope.io)
    .build()
