package com.thomaskioko.tvmaniac.data.logout.testing

import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler

public class FakeLogoutHandler : LogoutHandler {

    public var accountDataCleared: Boolean = false
        private set

    public var accountAndTrackingDataCleared: Boolean = false
        private set

    override suspend fun clearAccountData() {
        accountDataCleared = true
    }

    override suspend fun clearAccountAndTrackingData() {
        accountAndTrackingDataCleared = true
    }
}
