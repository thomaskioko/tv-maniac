package com.thomaskioko.tvmaniac.accountmanager.api

import kotlinx.coroutines.flow.Flow

public interface AccountManager {
    public val activeProvider: Flow<AccountProvider?>
    public val isConnected: Flow<Boolean>
    public val connectionEvents: Flow<AccountProvider>
    public fun getActiveProvider(): AccountProvider?
}
