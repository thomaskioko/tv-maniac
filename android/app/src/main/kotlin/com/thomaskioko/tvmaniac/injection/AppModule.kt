package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Named("trakt-client-id")
    @Provides
    fun provideTraktClientId(): String = BuildConfig.TRAKT_CLIENT_ID

    @Singleton
    @Named("trakt-client-secret")
    @Provides
    fun provideTraktClientSecret(): String = BuildConfig.TRAKT_CLIENT_SECRET

    @Singleton
    @Named("trakt-auth-redirect-uri")
    @Provides
    fun provideAuthRedirectUri(): String = BuildConfig.TRAKT_REDIRECT_URI

    @Singleton
    @Named("tmdb-api-key")
    @Provides
    fun provideTmdbApiKey(): String = BuildConfig.TMDB_API_KEY

    @Singleton
    @Named("app-build")
    @Provides
    fun provideIsDebug(): Boolean = BuildConfig.DEBUG

}
