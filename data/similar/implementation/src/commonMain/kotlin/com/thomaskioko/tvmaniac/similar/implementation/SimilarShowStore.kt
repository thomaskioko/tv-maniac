package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class SimilarShowStore(
    private val remoteDataSource: TraktShowsRemoteDataSource,
    private val similarShowsDao: SimilarShowsDao,
    private val showsDao: ShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val scope: AppCoroutineScope,
    private val logger: KermitLogger,
) : Store<Long, List<SimilarShows>> by StoreBuilder.from(
    fetcher = Fetcher.of { id ->

        when (val apiResult = remoteDataSource.getSimilarShows(id)) {
            is ApiResponse.Success -> apiResult.body.responseToShow()

            is ApiResponse.Error.GenericError -> {
                logger.error("SimilarShowStore GenericError", "${apiResult.errorMessage}")
                throw Throwable("${apiResult.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("SimilarShowStore HttpError", "${apiResult.code} - ${apiResult.errorBody}")
                throw Throwable("${apiResult.code} - ${apiResult.errorMessage}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("SimilarShowStore SerializationError", "${apiResult.errorMessage}")
                throw Throwable("${apiResult.errorMessage}")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { id -> similarShowsDao.observeSimilarShows(id) },
        writer = { id, list ->

            list.forEach {
                showsDao.upsert(it.toShow())

                similarShowsDao.upsert(
                    similarShowId = it.id.id,
                    showId = id,
                )
            }

            requestManagerRepository.insert(
                LastRequest(
                    id = id + list.size,
                    entityId = id,
                    requestType = "SIMILAR_SHOWS",
                ),
            )
        },
        delete = similarShowsDao::delete,
        deleteAll = similarShowsDao::deleteAll,
    ),
)
    .scope(scope.io)
    .build()
