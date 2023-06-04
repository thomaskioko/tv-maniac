package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class ShowStore(
    private val showsDao: ShowsDao,
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val mapper: ShowsResponseMapper,
    private val scope: AppCoroutineScope,
    private val logger: KermitLogger,
) : Store<Long, ShowById> by StoreBuilder.from<Long, ShowById, ShowById>(
    fetcher = Fetcher.of { traktId ->
        when (val apiResult = traktRemoteDataSource.getSeasonDetails(traktId)) {
            is ApiResponse.Success -> {
                mapper.responseToShow(apiResult.body)
            }

            is ApiResponse.Error.GenericError -> {
                logger.error("GenericError", "${apiResult.errorMessage}")
                throw Throwable("${apiResult.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("HttpError", "${apiResult.code} - ${apiResult.errorBody?.message}")
                throw Throwable("${apiResult.code} - ${apiResult.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("SerializationError", "$apiResult")
                throw Throwable("$apiResult")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { traktId -> showsDao.observeTvShow(traktId) },
        writer = { _, show ->

            showsDao.insert(mapper.toShow(show))
        },
    ),
)
    .scope(scope.io)
    .build()
