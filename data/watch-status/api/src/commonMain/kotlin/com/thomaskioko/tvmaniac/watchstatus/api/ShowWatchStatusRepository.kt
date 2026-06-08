package com.thomaskioko.tvmaniac.watchstatus.api

public interface ShowWatchStatusRepository {
    public suspend fun refresh(showId: Long)
}
