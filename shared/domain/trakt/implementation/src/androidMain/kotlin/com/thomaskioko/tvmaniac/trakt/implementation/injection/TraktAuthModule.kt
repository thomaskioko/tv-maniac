package com.thomaskioko.tvmaniac.trakt.implementation.injection

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.trakt.api.TraktAuthState
import com.thomaskioko.tvmaniac.trakt.api.TraktManager
import com.thomaskioko.tvmaniac.trakt.implementation.TraktManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.ResponseTypeValues
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object TraktAuthModule {

    @Singleton
    @Provides
    @Named("auth")
    fun provideAuthSharedPrefs(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("trakt_auth", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideTraktManager(
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher mainDispatcher: CoroutineDispatcher,
        @Named("auth") authPrefs: SharedPreferences,
    ): TraktManager = TraktManagerImpl(ioDispatcher, mainDispatcher, authPrefs)

    @Provides
    fun provideAuthState(traktManager: TraktManager): TraktAuthState = runBlocking {
        traktManager.state.first()
    }

    @Singleton
    @Provides
    fun provideAuthConfig(): AuthorizationServiceConfiguration {
        return AuthorizationServiceConfiguration(
            Uri.parse("https://trakt.tv/oauth/authorize"),
            Uri.parse("https://trakt.tv/oauth/token")
        )
    }

    @Provides
    fun provideAuthRequest(
        serviceConfig: AuthorizationServiceConfiguration,
        @Named("trakt-client-id") clientId: String,
        @Named("trakt-auth-redirect-uri") redirectUri: String
    ): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri.toUri()
        ).apply {
            // Disable PKCE since Trakt does not support it
            setCodeVerifier(null)
        }.build()
    }


    @Singleton
    @Provides
    fun provideClientAuth(
        @Named("trakt-client-secret") clientSecret: String
    ): ClientAuthentication {
        return ClientSecretBasic(clientSecret)
    }


}
