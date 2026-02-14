package com.thomaskioko.tvmaniac.inject

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.notifications.implementation.DebugNotificationManager
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class DebugNotificationInitializer(
    private val coroutineScope: AppCoroutineScope,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val debugNotificationManager: Lazy<DebugNotificationManager>,
) : AppInitializer {

    override fun init() {
        coroutineScope.io.launch {
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
