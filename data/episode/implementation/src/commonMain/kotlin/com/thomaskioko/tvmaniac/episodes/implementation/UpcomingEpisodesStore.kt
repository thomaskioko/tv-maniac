package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPCOMING_EPISODES
import com.thomaskioko.tvmaniac.trakt.api.TraktCalendarRemoteDataSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import kotlin.time.Instant

public data class UpcomingEpisodesParams(
    val startDate: String,
    val days: Int,
)

@Inject
public class UpcomingEpisodesStore(
    private val calendarDataSource: TraktCalendarRemoteDataSource,
    private val episodesDao: EpisodesDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<UpcomingEpisodesParams, Unit> by storeBuilder(
    fetcher = apiFetcher { params: UpcomingEpisodesParams ->
        calendarDataSource.getMyShowsCalendar(params.startDate, params.days)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { flowOf(Unit) },
        writer = { _, response ->
            response.forEach { calendarEntry ->
                val traktId = calendarEntry.show.ids.trakt
                val firstAiredEpoch = Instant.parse(calendarEntry.firstAired).toEpochMilliseconds()
                episodesDao.updateFirstAired(
                    showId = traktId,
                    seasonNumber = calendarEntry.episode.seasonNumber.toLong(),
                    episodeNumber = calendarEntry.episode.episodeNumber.toLong(),
                    firstAired = firstAiredEpoch,
                )
            }
            requestManagerRepository.upsert(
                entityId = UPCOMING_EPISODES.requestId,
                requestType = UPCOMING_EPISODES.name,
            )
        },
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = UPCOMING_EPISODES.name,
                threshold = UPCOMING_EPISODES.duration,
            )
        }
    },
).build()
