package com.thomaskioko.tvmaniac.datastore.testing

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.AuthState
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeDatastoreRepository : DatastoreRepository {

    private val appThemeFlow = MutableStateFlow(AppTheme.SYSTEM_THEME)
    private val authStateFlow: Channel<AuthState> = Channel(Channel.UNLIMITED)
    private val languageFlow: Channel<String> = Channel(Channel.UNLIMITED)
    private val listStyleFlow: Channel<ListStyle> = Channel(Channel.UNLIMITED)

    suspend fun setTheme(appTheme: AppTheme) {
        appThemeFlow.value = appTheme
    }

    suspend fun setAuthState(authState: AuthState) {
        authStateFlow.send(authState)
    }

    suspend fun setLanguage(languageCode: String) {
        languageFlow.send(languageCode)
    }

    override fun saveTheme(appTheme: AppTheme) {
        appThemeFlow.value = appTheme
    }

    override fun observeTheme(): Flow<AppTheme> = appThemeFlow.asStateFlow()

    override fun clearAuthState() {
        // no -op
    }

    override fun observeAuthState(): Flow<AuthState> = authStateFlow.receiveAsFlow()

    override suspend fun saveAuthState(authState: AuthState) {
        // no -op
    }

    override suspend fun getAuthState(): AuthState = AuthState()

    override suspend fun saveLanguage(languageCode: String) {
        // no -op
    }

    override fun observeLanguage(): Flow<String> = languageFlow.receiveAsFlow()

    override suspend fun saveListStyle(listStyle: ListStyle) {
        // no-op
    }

    override fun observeListStyle(): Flow<ListStyle> = listStyleFlow.receiveAsFlow()
}

val authenticatedAuthState = AuthState(
    isAuthorized = true,
    accessToken = "wrwjqoi294930uknfasf",
    refreshToken = "wrwjqoi294930uknfasf",
)
