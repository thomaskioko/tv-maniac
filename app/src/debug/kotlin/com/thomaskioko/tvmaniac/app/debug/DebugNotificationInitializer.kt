package com.thomaskioko.tvmaniac.app.debug

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.notifications.implementation.DebugNotificationManager
import com.thomaskioko.tvmaniac.domain.settings.ObserveSettingsPreferencesInteractor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Inject
public class DebugNotificationInitializer(
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    private val observeSettingsPreferencesInteractor: Lazy<ObserveSettingsPreferencesInteractor>,
    private val debugNotificationManager: Lazy<DebugNotificationManager>,
) {

    public fun init() {
        coroutineScope.launch {
            observeSettingsPreferencesInteractor.value(Unit)
            observeSettingsPreferencesInteractor.value.flow
                .map { it.episodeNotificationsEnabled }
                .collect { enabled ->
                    when {
                        enabled -> debugNotificationManager.value.show()
                        else -> debugNotificationManager.value.dismiss()
                    }
                }
        }
    }
}
