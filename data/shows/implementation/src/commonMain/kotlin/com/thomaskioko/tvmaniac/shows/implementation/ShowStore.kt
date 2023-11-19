package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class ShowStore(
    private val remoteDataSource: TraktShowsRemoteDataSource,
    private val showsDao: ShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val mapper: DiscoverResponseMapper,
    private val scope: AppCoroutineScope,
    private val logger: KermitLogger,
) : Store<Long, ShowById> by StoreBuilder.from<Long, ShowById, ShowById>(
    fetcher = Fetcher.of { traktId ->
        when (val apiResult = remoteDataSource.getSeasonDetails(traktId)) {
            is ApiResponse.Success -> {
                mapper.responseToShow(apiResult.body)
            }

            is ApiResponse.Error.GenericError -> {
                logger.error("ShowStore GenericError", "${apiResult.errorMessage}")
                throw Throwable("${apiResult.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("ShowStore HttpError", "${apiResult.code} - ${apiResult.errorBody}")
                throw Throwable("${apiResult.code} - ${apiResult.errorMessage}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("ShowStore SerializationError", "${apiResult.errorMessage}")
                throw Throwable("${apiResult.errorMessage}")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { traktId -> showsDao.observeTvShow(traktId) },
        writer = { id, show ->

            showsDao.upsert(mapper.toShow(show))

            requestManagerRepository.insert(
                LastRequest(
                    id = id + show.id.id,
                    entityId = id,
                    requestType = "SHOW_DETAILS",
                ),
            )
        },
    ),
)
    .scope(scope.io)
    .build()
