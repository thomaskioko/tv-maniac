package com.thomaskioko.tvmaniac.data.trailers

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.dsl.module

actual fun trailerDomainModule() : KoinModule = module {  }

@InstallIn(SingletonComponent::class)
@Module
object FollowingModule {

    @Provides
    fun provideTrailersStateMachine(
        repository: TrailerRepository
    ): TrailersStateMachine = TrailersStateMachine(repository)
}
