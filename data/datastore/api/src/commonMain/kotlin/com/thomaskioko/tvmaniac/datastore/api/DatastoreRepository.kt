package com.thomaskioko.tvmaniac.datastore.api

import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    fun saveTheme(appTheme: AppTheme)

    fun observeTheme(): Flow<AppTheme>

    fun clearAuthState()

    fun observeAuthState(): Flow<AuthState>

    suspend fun saveAuthState(authState: AuthState)

    suspend fun getAuthState(): AuthState?
}

enum class AppTheme(val value: String) {
    LIGHT_THEME("Light Theme"),
    DARK_THEME("Light Theme"),
    SYSTEM_THEME("Light Theme"),
}
