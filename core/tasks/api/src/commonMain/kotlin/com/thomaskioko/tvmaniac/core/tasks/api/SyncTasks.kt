package com.thomaskioko.tvmaniac.core.tasks.api

public interface SyncTasks {
    public fun setup(): Unit = Unit
    public fun scheduleLibrarySync()
    public fun cancelLibrarySync()
}
