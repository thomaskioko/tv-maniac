package com.thomaskioko.tvmaniac.network.di

import android.content.Context
import com.thomaskioko.tvmaniac.core.util.AppContext
import com.thomaskioko.tvmaniac.core.util.network.ObserveConnectionState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideObserveConnectionState(
        @ApplicationContext context: Context
    ) = ObserveConnectionState(context as AppContext)
}
