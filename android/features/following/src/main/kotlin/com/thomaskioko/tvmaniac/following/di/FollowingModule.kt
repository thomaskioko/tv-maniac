package com.thomaskioko.tvmaniac.following.di

import com.thomaskioko.tvmaniac.domain.following.api.FollowingStateMachine
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FollowingModule {

    @Singleton
    @Provides
    fun provideFollowingStateMachine(
        repository: TraktRepository
    ): FollowingStateMachine = FollowingStateMachine(repository)

}