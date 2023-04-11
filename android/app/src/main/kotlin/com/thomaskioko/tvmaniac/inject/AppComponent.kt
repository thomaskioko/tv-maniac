package com.thomaskioko.tvmaniac.inject

import com.thomaskioko.tvmaniac.BuildConfig
import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.model.TraktOAuthInfo
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface AppComponent {

    @ApplicationScope
    @Provides
    fun provideTraktOAuthInfo(): TraktOAuthInfo = TraktOAuthInfo(
        clientId = BuildConfig.TRAKT_CLIENT_ID,
        clientSecret = BuildConfig.TRAKT_CLIENT_SECRET,
        redirectUri = BuildConfig.TRAKT_REDIRECT_URI,
    )

    @ApplicationScope
    @Provides
    fun provideAppConfig(): AppConfig = AppConfig(
        isDebug = BuildConfig.DEBUG,
        tmdbApiKey = BuildConfig.TMDB_API_KEY
    )
}