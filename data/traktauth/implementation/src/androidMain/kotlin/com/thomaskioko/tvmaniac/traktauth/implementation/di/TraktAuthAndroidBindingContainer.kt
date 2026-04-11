package com.thomaskioko.tvmaniac.traktauth.implementation.di

import android.app.Application
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.util.api.BuildConfig
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
    ): AuthorizationRequest = AuthorizationRequest.Builder(
        configuration,
        BuildConfig.TRAKT_CLIENT_ID,
        ResponseTypeValues.CODE,
        BuildConfig.TRAKT_REDIRECT_URI.toUri(),
    )
        .apply { setCodeVerifier(null) }
        .build()

    @Provides
    public fun provideClientAuth(): ClientAuthentication =
        ClientSecretPost(BuildConfig.TRAKT_CLIENT_SECRET)

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideAuthorizationService(application: Application): AuthorizationService =
        AuthorizationService(application)
}
