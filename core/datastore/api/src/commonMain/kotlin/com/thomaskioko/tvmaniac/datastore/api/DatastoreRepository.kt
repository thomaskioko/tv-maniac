package com.thomaskioko.tvmaniac.datastore.api

import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    fun saveTheme(theme: Theme)
    fun observeTheme(): Flow<Theme>

    fun clearAuthState()
    fun observeAuthState(): Flow<AuthState>
    suspend fun saveAuthState(authState: AuthState)
    suspend fun getAuthState(): AuthState
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM,
}
