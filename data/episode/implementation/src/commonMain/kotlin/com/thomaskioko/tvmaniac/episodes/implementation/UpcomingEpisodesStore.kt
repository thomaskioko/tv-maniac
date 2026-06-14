package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.calendar.CalendarRemoteDataSource
import com.thomaskioko.tvmaniac.data.calendar.RemoteCalendarEntry
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPCOMING_EPISODES
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
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
    private val activeSource: () -> CalendarRemoteDataSource?,
    private val episodesDao: EpisodesDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<UpcomingEpisodesParams, Unit> by storeBuilder(
    fetcher = Fetcher.of { params: UpcomingEpisodesParams ->
        val source = activeSource() ?: throw AuthenticationException("No active calendar provider")
        source.getCalendarEntries(startDate = params.startDate, days = params.days).getOrThrow()
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { flowOf(Unit) },
        writer = { _, response: List<RemoteCalendarEntry> ->
            response.forEach { entry ->
                val firstAiredEpoch = Instant.parse(entry.firstAiredIso).toEpochMilliseconds()
                episodesDao.updateFirstAired(
                    showId = entry.tmdbId,
                    seasonNumber = entry.seasonNumber.toLong(),
                    episodeNumber = entry.episodeNumber.toLong(),
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
