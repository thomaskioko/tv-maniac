package com.thomaskioko.tvmaniac.traktauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState

public interface AuthStore {
    public suspend fun get(): AuthState?
    public suspend fun save(state: AuthState)
    public suspend fun clear()
}
