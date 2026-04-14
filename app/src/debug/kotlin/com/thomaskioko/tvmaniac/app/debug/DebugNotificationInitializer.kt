package com.thomaskioko.tvmaniac.app.debug

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.notifications.implementation.DebugNotificationManager
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
public class DebugNotificationInitializer(
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val debugNotificationManager: Lazy<DebugNotificationManager>,
) {

    public fun init() {
        coroutineScope.launch {
            datastoreRepository.value.observeEpisodeNotificationsEnabled()
                .collect { enabled ->
                    when {
                        enabled -> debugNotificationManager.value.show()
                        else -> debugNotificationManager.value.dismiss()
                    }
                }
        }
    }
}
