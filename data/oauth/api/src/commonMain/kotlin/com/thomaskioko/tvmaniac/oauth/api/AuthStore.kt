package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource

public interface AuthStore {
    public suspend fun get(provider: SyncProviderSource): AuthState?
    public suspend fun save(provider: SyncProviderSource, state: AuthState)
    public suspend fun clear(provider: SyncProviderSource)
}
