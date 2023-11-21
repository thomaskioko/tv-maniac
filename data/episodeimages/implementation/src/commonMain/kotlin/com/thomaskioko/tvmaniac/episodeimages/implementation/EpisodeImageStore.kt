package com.thomaskioko.tvmaniac.episodeimages.implementation

import com.thomaskioko.tvmaniac.core.db.EpisodeImage
import com.thomaskioko.tvmaniac.core.db.Episode_image
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.Validator
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Inject
class EpisodeImageStore(
    private val tmdbNetworkDataSource: TmdbNetworkDataSource,
    private val episodeImageDao: EpisodeImageDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dbTransactionRunner: DatabaseTransactionRunner,
    private val formatterUtil: FormatterUtil,
    private val logger: KermitLogger,
    private val scope: AppCoroutineScope,
) : Store<Long, List<EpisodeImage>> by StoreBuilder.from(
    fetcher = Fetcher.of { id ->
        episodeImageDao.getEpisodeImage(id)
            .filter { it.tmdb_id != null && it.image_url == null }
            .map { episodeArt ->
                val apiResponse = tmdbNetworkDataSource.getEpisodeDetails(
                    tmdbShow = episodeArt.tmdb_id!!,
                    ssnNumber = episodeArt.season_number!!,
                    epNumber = episodeArt.episode_number.toLong(),
                )

                when (apiResponse) {
                    is ApiResponse.Success -> {
                        Episode_image(
                            id = Id(id = episodeArt.id.id),
                            tmdb_id = Id(id = episodeArt.tmdb_id!!),
                            image_url = apiResponse.body.imageUrl?.let {
                                formatterUtil.formatTmdbPosterPath(it)
                            },
                        )
                    }

                    is ApiResponse.Error -> {
                        logger.error("EpisodeImageStore", "$apiResponse")
                        throw Throwable("$apiResponse")
                    }
                }
            }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { id -> episodeImageDao.observeEpisodeImage(id) },
        writer = { _, imageList ->
            dbTransactionRunner {
                episodeImageDao.upsert(imageList)
            }
        },
        delete = episodeImageDao::delete,
        deleteAll = { dbTransactionRunner(episodeImageDao::deleteAll) },
    ),
).validator(
    Validator.by { result ->
        if (result.isNotEmpty()) {
            requestManagerRepository.isRequestExpired(
                entityId = result.first().tmdb_id!!,
                requestType = "EPISODE_IMAGE",
                threshold = 180.days,
            )
        } else {
            result.firstOrNull()?.tmdb_id?.let {
                requestManagerRepository.isRequestExpired(
                    entityId = it,
                    requestType = "EPISODE_IMAGE",
                    threshold = 1.hours,
                )
            } ?: run {
                true
            }
        }
    },
)
    .scope(scope.io)
    .build()
