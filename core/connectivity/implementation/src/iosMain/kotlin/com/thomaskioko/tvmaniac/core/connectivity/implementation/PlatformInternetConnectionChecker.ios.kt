package com.thomaskioko.tvmaniac.core.connectivity.implementation

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_queue_create
import kotlin.concurrent.Volatile

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public actual class PlatformInternetConnectionChecker : InternetConnectionChecker {

    private val monitorQueue = dispatch_queue_create(
        "com.thomaskioko.tvmaniac.connectivity",
        null,
    )
    private val monitor = nw_path_monitor_create()

    @Volatile
    private var connected: Boolean = true

    init {
        nw_path_monitor_set_queue(monitor, monitorQueue)
        nw_path_monitor_set_update_handler(monitor) { path ->
            connected = path != null && nw_path_get_status(path) == nw_path_status_satisfied
        }
        nw_path_monitor_start(monitor)
    }

    public actual override fun isConnected(): Boolean = connected
}
