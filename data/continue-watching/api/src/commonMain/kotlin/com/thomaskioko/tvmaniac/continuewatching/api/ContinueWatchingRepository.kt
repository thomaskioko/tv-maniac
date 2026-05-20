package com.thomaskioko.tvmaniac.continuewatching.api

public interface ContinueWatchingRepository {

    public suspend fun sync(forceRefresh: Boolean = false)
}
