package com.thomaskioko.tvmaniac.traktauth.api

public interface TraktAuthTasks {
    public fun setup(): Unit = Unit
    public fun scheduleTokenRefresh()
    public fun cancelTokenRefresh()
}
