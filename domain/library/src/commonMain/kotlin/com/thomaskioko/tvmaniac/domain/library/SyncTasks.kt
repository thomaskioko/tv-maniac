package com.thomaskioko.tvmaniac.domain.library

public interface SyncTasks {
    public fun setup(): Unit = Unit
    public fun scheduleAndRunLibrarySync()
    public fun cancelLibrarySync()
}
