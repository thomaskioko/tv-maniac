package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbNetworkDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class TrailerStore(
    private val apiService: TmdbNetworkDataSource,
    private val trailerDao: TrailerDao,
    private val showsDao: ShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val logger: KermitLogger,
    private val scope: AppCoroutineScope,
) : Store<Long, List<Trailers>> by StoreBuilder.from<Long, List<Trailers>, List<Trailers>>(
    fetcher = Fetcher.of { id ->

        val show = showsDao.getTvShow(id)

        when (val apiResult = apiService.getTrailers(show.tmdb_id!!)) {
            is ApiResponse.Success -> apiResult.body.results.toEntity(id)

            is ApiResponse.Error.GenericError -> {
                logger.error("TrailerStore GenericError", "$apiResult")
                throw Throwable("${apiResult.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("TrailerStore HttpError", "$apiResult")
                throw Throwable("${apiResult.code} - ${apiResult.errorMessage}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("TrailerStore SerializationError", "${apiResult.errorMessage}")
                throw Throwable("${apiResult.errorMessage}")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { id -> trailerDao.observeTrailersById(id) },
        writer = { id, list ->
            trailerDao.insert(list)
            requestManagerRepository.insert(
                LastRequest(
                    id = list.first().trakt_id,
                    entityId = id,
                    requestType = "TRAILERS",
                ),
            )
        },
        delete = trailerDao::delete,
        deleteAll = trailerDao::deleteAll,
    ),
)
    .scope(scope.io)
    .build()
