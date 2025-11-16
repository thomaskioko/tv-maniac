package com.thomaskioko.tvmaniac.traktauth.implementation

import android.app.Application
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.buildconfig.api.BuildConfig
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
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
interface TraktAuthAndroidComponent {

    @Provides
    @SingleIn(ActivityScope::class)
    fun provideAuthConfig(): AuthorizationServiceConfiguration {
        return AuthorizationServiceConfiguration(
            "https://trakt.tv/oauth/authorize".toUri(),
            "https://trakt.tv/oauth/token".toUri(),
        )
    }

    @Provides
    @SingleIn(ActivityScope::class)
    fun provideAuthRequest(
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
    fun provideClientAuth(): ClientAuthentication =
        ClientSecretBasic(BuildConfig.TRAKT_CLIENT_SECRET)

    @Provides
    @SingleIn(ActivityScope::class)
    fun provideAuthorizationService(application: Application): AuthorizationService =
        AuthorizationService(application)
}
