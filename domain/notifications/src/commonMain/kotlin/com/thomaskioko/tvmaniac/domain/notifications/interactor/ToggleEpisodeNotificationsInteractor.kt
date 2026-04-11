package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
public class ToggleEpisodeNotificationsInteractor(
    private val datastoreRepository: DatastoreRepository,
) : Interactor<ToggleEpisodeNotificationsInteractor.Params>() {

    public data class Params(val enabled: Boolean)

    override suspend fun doWork(params: Params) {
        if (!params.enabled) {
            datastoreRepository.setEpisodeNotificationsEnabled(false)
            return
        }

        datastoreRepository.setShowNotificationRationale(true)
    }
}
