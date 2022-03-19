package com.thomaskioko.tvmaniac.injection

import android.content.Context
import com.thomaskioko.tvmaniac.core.util.AppContext
import com.thomaskioko.tvmaniac.core.util.network.ObserveConnectionState
import com.thomaskioko.tvmaniac.remote.KtorClientFactory
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.remote.api.TvShowsServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return KtorClientFactory().build()
    }

    @Singleton
    @Provides
    fun provideTvShowService(
        httpClient: HttpClient
    ): TvShowsService = TvShowsServiceImpl(httpClient)

    @Singleton
    @Provides
    fun provideObserveConnectionState(
        @ApplicationContext context: Context
    ) = ObserveConnectionState(context as AppContext)
}
