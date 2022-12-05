package com.thomaskioko.tvmaniac.following.di

import com.thomaskioko.tvmaniac.following.ObserveFollowingInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InteractorModule {

    @Singleton
    @Provides
    fun provideObserveFollowingInteractor(
        repository: TraktRepository
    ): ObserveFollowingInteractor = ObserveFollowingInteractor(repository)

}
