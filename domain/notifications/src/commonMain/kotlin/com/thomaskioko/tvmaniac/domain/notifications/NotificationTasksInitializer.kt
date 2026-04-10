package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.core.base.di.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Inject
public class NotificationTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val notificationManager: Lazy<NotificationManager>,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val coroutineScope: AppCoroutineScope,
    private val logger: Logger,
) : AppInitializer {

    override fun init() {
        coroutineScope.io.launch {
            combine(
                traktAuthRepository.value.state,
                datastoreRepository.value.observeEpisodeNotificationsEnabled(),
                datastoreRepository.value.observeBackgroundSyncEnabled(),
            ) { authState, notificationsEnabled, syncEnabled ->
                authState == TraktAuthState.LOGGED_IN && notificationsEnabled && syncEnabled
            }
                .distinctUntilChanged()
                .collect { shouldSchedule ->
                    if (shouldSchedule) {
                        logger.debug(TAG, "Scheduling episode notifications")
                        scheduler.scheduleAndExecute(EpisodeNotificationWorker.REQUEST)
                    } else {
                        logger.debug(TAG, "Cancelling episode notifications")
                        scheduler.cancel(EpisodeNotificationWorker.WORKER_NAME)
                        notificationManager.value.cancelAllNotifications()
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "NotificationTasksInitializer"
    }
}
