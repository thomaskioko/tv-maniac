package com.thomaskioko.tvmaniac.domain.notifications

public interface NotificationTasks {
    public fun setup(): Unit = Unit
    public fun scheduleEpisodeNotifications()
    public fun scheduleAndRunEpisodeNotifications()
    public fun cancelEpisodeNotifications()
    public fun rescheduleBackgroundTask(): Unit = Unit
}
