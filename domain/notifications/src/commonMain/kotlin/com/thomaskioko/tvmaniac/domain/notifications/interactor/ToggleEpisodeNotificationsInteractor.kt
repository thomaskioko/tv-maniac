package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.notifications.EpisodeNotificationWorker
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class ToggleEpisodeNotificationsInteractor(
    private val datastoreRepository: DatastoreRepository,
    private val scheduler: BackgroundTaskScheduler,
    private val traktAuthRepository: TraktAuthRepository,
) : Interactor<ToggleEpisodeNotificationsInteractor.Params>() {

    public data class Params(val enabled: Boolean)

    override suspend fun doWork(params: Params) {
        if (!params.enabled) {
            datastoreRepository.setEpisodeNotificationsEnabled(false)
            return
        }

        val permissionAsked = datastoreRepository.getNotificationPermissionAsked()
        if (!permissionAsked) {
            datastoreRepository.setShowNotificationRationale(true)
            return
        }

        datastoreRepository.setEpisodeNotificationsEnabled(true)
        if (traktAuthRepository.isLoggedIn()) {
            scheduler.scheduleAndExecute(EpisodeNotificationWorker.REQUEST)
        }
    }
}
