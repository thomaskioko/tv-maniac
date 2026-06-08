package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState

public interface AuthStore {
    public suspend fun get(provider: AccountProvider): AuthState?
    public suspend fun save(provider: AccountProvider, state: AuthState)
    public suspend fun clear(provider: AccountProvider)
}
