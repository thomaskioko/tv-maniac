package com.thomaskioko.tvmaniac.data.logout.testing

import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler

public class FakeLogoutHandler : LogoutHandler {

    public var cleared: Boolean = false
        private set

    override suspend fun clear() {
        cleared = true
    }
}
