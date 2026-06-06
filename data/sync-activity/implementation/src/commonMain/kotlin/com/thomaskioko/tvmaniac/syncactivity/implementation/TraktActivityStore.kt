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
import com.thomaskioko.tvmaniac.syncprovider.api.RemoteActivitySource
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class TraktActivityStore(
    private val remoteActivitySource: RemoteActivitySource,
    private val activityDao: TraktActivityDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val dateTimeProvider: DateTimeProvider,
) : Store<Unit, List<TraktLastActivity>> by storeBuilder(
    fetcher = apiFetcher { remoteActivitySource.getLastActivities() },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: Unit -> activityDao.observeAll() },
        writer = { _: Unit, activities: Map<ActivityType, Instant> ->
            val now = dateTimeProvider.now()

            activities.forEach { (activityType, timestamp) ->
                activityDao.upsert(activityType, timestamp, now)
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
