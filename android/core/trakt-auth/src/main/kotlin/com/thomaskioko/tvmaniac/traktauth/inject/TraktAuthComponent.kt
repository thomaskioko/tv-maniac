package com.thomaskioko.tvmaniac.traktauth.inject

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.base.model.TraktOAuthInfo
import com.thomaskioko.tvmaniac.base.scope.ActivityScope
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManagerImpl
import com.thomaskioko.tvmaniac.traktauth.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.model.TraktAuthState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Provides
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.ResponseTypeValues

interface TraktAuthComponent {

    @ApplicationScope
    @Provides
    fun provideTraktAuthState(traktAuthRepository: TraktAuthRepository): TraktAuthState =
        runBlocking {
            traktAuthRepository.state.first()
        }

    @ApplicationScope
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
        oauthInfo: TraktOAuthInfo,
    ): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            serviceConfig,
            oauthInfo.clientId,
            ResponseTypeValues.CODE,
            oauthInfo.redirectUri.toUri()
        ).apply {
            setCodeVerifier(null)
        }.build()
    }


    @ApplicationScope
    @Provides
    fun provideClientAuth(
        oauthInfo: TraktOAuthInfo,
    ): ClientAuthentication = ClientSecretBasic(oauthInfo.clientSecret)

    @ApplicationScope
    @Provides
    fun provideAuthSharedPrefs(
        context: Application
    ): SharedPreferences = context.getSharedPreferences("trakt_auth", Context.MODE_PRIVATE)

    @ApplicationScope
    @Provides
    fun provideAuthorizationService(
        application: Application
    ): AuthorizationService = AuthorizationService(application)
}

interface TraktAuthManagerComponent {

    @ActivityScope
    @Provides
    fun provideTraktAuthManager(bind: TraktAuthManagerImpl): TraktAuthManager = bind
}