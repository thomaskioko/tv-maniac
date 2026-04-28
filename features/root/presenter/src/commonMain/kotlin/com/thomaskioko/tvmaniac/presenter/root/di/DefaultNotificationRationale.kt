package com.thomaskioko.tvmaniac.presenter.root.di

import com.thomaskioko.root.nav.NotificationRationale
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultNotificationRationale(
    private val datastoreRepository: DatastoreRepository,
) : NotificationRationale {

    override suspend fun showIfNeeded() {
        combine(
            datastoreRepository.observeNotificationPermissionAsked(),
            datastoreRepository.observeShowNotificationRationale(),
        ) { hasAsked, isRationaleShowing ->
            !hasAsked && !isRationaleShowing
        }
            .filter { it }
            .take(1)
            .collect { datastoreRepository.setShowNotificationRationale(true) }
    }
}
