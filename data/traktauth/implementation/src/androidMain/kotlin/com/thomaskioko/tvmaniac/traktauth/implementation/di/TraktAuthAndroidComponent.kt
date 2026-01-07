package com.thomaskioko.tvmaniac.traktauth.implementation.di

import android.app.Application
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import me.tatarka.inject.annotations.Provides
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.ResponseTypeValues
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(ActivityScope::class)
public interface TraktAuthAndroidComponent {

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideAuthConfig(): AuthorizationServiceConfiguration {
        return AuthorizationServiceConfiguration(
            "https://trakt.tv/oauth/authorize".toUri(),
            "https://trakt.tv/oauth/token".toUri(),
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
        ClientSecretBasic(BuildConfig.TRAKT_CLIENT_SECRET)

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideAuthorizationService(application: Application): AuthorizationService =
        AuthorizationService(application)
}
