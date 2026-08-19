package com.thomaskioko.tvmaniac.data.logout.api

public interface LogoutHandler {
    public suspend fun clearAccountData()

    public suspend fun clearAccountAndTrackingData()
}
