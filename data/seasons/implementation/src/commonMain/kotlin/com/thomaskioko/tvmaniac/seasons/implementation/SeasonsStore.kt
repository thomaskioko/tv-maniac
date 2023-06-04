package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class SeasonsStore(
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val seasonsDao: SeasonsDao,
    private val scope: AppCoroutineScope,
    private val logger: KermitLogger,
) : Store<Long, List<Seasons>> by StoreBuilder.from<Long, List<Seasons>, List<Seasons>>(
    fetcher = Fetcher.of { id ->
        when (val response = traktRemoteDataSource.getShowSeasons(id)) {
            is ApiResponse.Success -> response.body.toSeasonCacheList(id)
            is ApiResponse.Error.GenericError -> {
                logger.error("GenericError", "$response")
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("HttpError", "$response")
                throw Throwable("${response.code} - ${response.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("SerializationError", "$response")
                throw Throwable("$response")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = seasonsDao::observeSeasons,
        writer = { _, list -> seasonsDao.insertSeasons(list) },
        delete = seasonsDao::delete,
        deleteAll = seasonsDao::deleteAll,
    ),
)
    .scope(scope.io)
    .build()
