package com.thomaskioko.tvmaniac.connectedaccount.api

import kotlinx.coroutines.flow.Flow

public interface ConnectedAccountRepository {
    public val activeProvider: Flow<ConnectedProvider?>
    public val isConnected: Flow<Boolean>
    public val connectionEvents: Flow<ConnectedProvider>
    public fun activeProviderOrNull(): ConnectedProvider?
}
