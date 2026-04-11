package com.thomaskioko.tvmaniac.traktauth.implementation.di

import android.app.Application
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.trakt.api.TraktConfig
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretPost
import net.openid.appauth.ResponseTypeValues

@BindingContainer
@ContributesTo(ActivityScope::class)
public object TraktAuthAndroidBindingContainer {

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideAuthConfig(): AuthorizationServiceConfiguration {
        return AuthorizationServiceConfiguration(
            "https://trakt.tv/oauth/authorize".toUri(),
            "https://api.trakt.tv/oauth/token".toUri(),
        )
    }

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideAuthRequest(
        configuration: AuthorizationServiceConfiguration,
        traktConfig: TraktConfig,
    ): AuthorizationRequest = AuthorizationRequest.Builder(
        configuration,
        traktConfig.clientId,
        ResponseTypeValues.CODE,
        traktConfig.redirectUri.toUri(),
    )
        .apply { setCodeVerifier(null) }
        .build()

    @Provides
    public fun provideClientAuth(traktConfig: TraktConfig): ClientAuthentication =
        ClientSecretPost(traktConfig.clientSecret)

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideAuthorizationService(application: Application): AuthorizationService =
        AuthorizationService(application)
}
