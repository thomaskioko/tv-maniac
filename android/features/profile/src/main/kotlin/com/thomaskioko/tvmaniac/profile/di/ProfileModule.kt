package com.thomaskioko.tvmaniac.profile.di

import com.thomaskioko.tvmaniac.profile.ProfileStateMachine
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import com.thomaskioko.tvmaniac.traktauth.TraktManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Singleton
    @Provides
    fun provideProfileStateMachine(
        repository: TraktRepository,
        traktManager: TraktManager,
    ): ProfileStateMachine = ProfileStateMachine(traktManager, repository)
}