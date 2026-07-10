package com.thomaskioko.tvmaniac.accountmanager.api

public data class ConnectedAccount(
    val provider: SyncProviderSource,
    val username: String? = null,
    val avatarUrl: String? = null,
    val isConnected: Boolean = false,
    val isActive: Boolean = false,
)
