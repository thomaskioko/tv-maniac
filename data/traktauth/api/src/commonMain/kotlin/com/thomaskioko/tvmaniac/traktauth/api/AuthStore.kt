package com.thomaskioko.tvmaniac.traktauth.api

interface AuthStore {
    suspend fun get(): AuthState?
    suspend fun save(state: AuthState)
    suspend fun clear()
}
