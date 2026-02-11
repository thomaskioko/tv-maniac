package com.thomaskioko.tvmaniac.domain.notifications

public interface NotificationTasks {
    public fun setup(): Unit = Unit
    public fun scheduleEpisodeNotifications()
    public fun cancelEpisodeNotifications()
}
