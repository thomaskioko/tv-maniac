package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class NotificationTasksInitializer(
    private val notificationTasks: Lazy<NotificationTasks>,
    private val notificationManager: Lazy<NotificationManager>,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val coroutineScope: AppCoroutineScope,
    private val logger: Logger,
) : AppInitializer {

    override fun init() {
        notificationTasks.value.setup()

        coroutineScope.io.launch {
            combine(
                traktAuthRepository.value.state,
                datastoreRepository.value.observeEpisodeNotificationsEnabled(),
            ) { authState, notificationsEnabled ->
                authState == TraktAuthState.LOGGED_IN && notificationsEnabled
            }
                .distinctUntilChanged()
                .collect { shouldSchedule ->
                    if (shouldSchedule) {
                        logger.debug(TAG, "Scheduling episode notifications")
                        notificationTasks.value.scheduleEpisodeNotifications()
                    } else {
                        logger.debug(TAG, "Cancelling episode notifications")
                        notificationTasks.value.cancelEpisodeNotifications()
                        notificationManager.value.cancelAllNotifications()
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "NotificationTasksInitializer"
    }
}
