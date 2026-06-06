package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrNull
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_CAST
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class ShowCastStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbNetworkDataSource: TmdbShowsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val castDao: CastDao,
    private val showIdResolver: ShowIdResolver,
    private val formatterUtil: FormatterUtil,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowCast>> by storeBuilder(
    fetcher = Fetcher.of { showId: Long ->
        coroutineScope {
            val tmdbId = tvShowsDao.getTmdbIdByShowId(showId)

            val traktDeferred = async {
                traktRemoteDataSource.getShowPeople(showId).getOrThrow()
            }
            val tmdbCreditsDeferred = tmdbId?.let { id ->
                async { tmdbNetworkDataSource.getShowCredits(id).getOrNull() }
            }

            val result = ShowCastResult(
                showId = showId,
                traktPeople = traktDeferred.await(),
                tmdbCredits = tmdbCreditsDeferred?.await(),
            )

            requestManagerRepository.upsert(
                entityId = showId,
                requestType = SHOW_CAST.name,
            )

            result
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, ShowCastResult, List<ShowCast>>(
        reader = { showId: Long ->
            castDao.observeShowCast(showId)
        },
        writer = { showId, result ->
            databaseTransactionRunner {
                val internalShowId = showIdResolver.showIdForTraktId(showId)
                    ?: return@databaseTransactionRunner

                val tmdbCastMap = result.tmdbCredits?.cast
                    ?.associateBy { it.id.toLong() }
                    ?: emptyMap()

                result.traktPeople.cast.forEach { castMember ->
                    val person = castMember.person
                    val tmdbId = person.ids.tmdb

                    if (tmdbId != null) {
                        val tmdbCast = tmdbCastMap[tmdbId]
                        val formattedProfilePath = tmdbCast?.profilePath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        }
                        castDao.upsert(
                            Casts(
                                id = Id(tmdbId),
                                trakt_id = Id(person.ids.trakt),
                                show_id = internalShowId,
                                season_id = null,
                                name = person.name,
                                character_name = castMember.characters.firstOrNull() ?: "",
                                profile_path = formattedProfilePath,
                                popularity = tmdbCast?.popularity,
                            ),
                        )
                    }
                }
            }
        },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { result ->
        withContext(dispatchers.io) {
            val showId = result.firstOrNull()?.show_trakt_id ?: return@withContext false
            !requestManagerRepository.isRequestExpired(
                entityId = showId,
                requestType = SHOW_CAST.name,
                threshold = SHOW_CAST.duration,
            )
        }
    },
).build()
