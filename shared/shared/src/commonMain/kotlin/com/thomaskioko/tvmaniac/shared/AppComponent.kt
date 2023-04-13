package com.thomaskioko.tvmaniac.shared

import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.model.TraktOAuthInfo
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.shared.BuildKonfig.TMDB_API_KEY
import com.thomaskioko.tvmaniac.shared.BuildKonfig.TRAKT_CLIENT_SECRET
import com.thomaskioko.tvmaniac.shared.BuildKonfig.TRAKT_REDIRECT_URI
import me.tatarka.inject.annotations.Provides

interface AppComponent {

    @ApplicationScope
    @Provides
    fun provideTraktOAuthInfo(): TraktOAuthInfo = TraktOAuthInfo(
        clientId = TMDB_API_KEY.replace("\"", ""),
        clientSecret = TRAKT_CLIENT_SECRET.replace("\"", ""),
        redirectUri = TRAKT_REDIRECT_URI.replace("\"", ""),
    )

    @ApplicationScope
    @Provides
    fun provideAppConfig(): AppConfig = AppConfig(
        isDebug = false,
        tmdbApiKey = TMDB_API_KEY.replace("\"", "")
    )
}