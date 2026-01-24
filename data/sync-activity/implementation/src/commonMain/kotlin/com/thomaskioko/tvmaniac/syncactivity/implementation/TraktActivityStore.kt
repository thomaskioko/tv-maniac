package com.thomaskioko.tvmaniac.syncactivity.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRAKT_ACTIVITIES
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.api.model.TraktLastActivity
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class TraktActivityStore(
    private val remoteDataSource: TraktSyncRemoteDataSource,
    private val activityDao: TraktActivityDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val dateTimeProvider: DateTimeProvider,
) : Store<Unit, List<TraktLastActivity>> by storeBuilder(
    fetcher = apiFetcher { remoteDataSource.getLastActivities() },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: Unit -> activityDao.observeAll() },
        writer = { _: Unit, response: TraktLastActivitiesResponse ->
            val now = dateTimeProvider.now()

            response.shows.watchlistedAt?.parseInstant()?.let { instant ->
                activityDao.upsert(ActivityType.SHOWS_WATCHLISTED, instant, now)
            }

            response.shows.favoritedAt?.parseInstant()?.let { instant ->
                activityDao.upsert(ActivityType.SHOWS_FAVORITED, instant, now)
            }

            response.episodes.watchedAt?.parseInstant()?.let { instant ->
                activityDao.upsert(ActivityType.EPISODES_WATCHED, instant, now)
            }

            requestManagerRepository.upsert(
                entityId = TRAKT_ACTIVITIES.requestId,
                requestType = TRAKT_ACTIVITIES.name,
            )
        },
        deleteAll = { activityDao.deleteAll() },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = TRAKT_ACTIVITIES.name,
                threshold = TRAKT_ACTIVITIES.duration,
            )
        }
    },
).build()

private fun String.parseInstant(): Instant? {
    return try {
        Instant.parse(this)
    } catch (_: IllegalArgumentException) {
        null
    }
}
