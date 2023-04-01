package com.thomaskioko.tvmaniac.shared.domain.discover

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.dsl.module
import javax.inject.Singleton

actual fun discoverDomainModule() : KoinModule = module {  }

@dagger.Module
@InstallIn(SingletonComponent::class)
object DiscoverModule {

    @Singleton
    @Provides
    fun provideDiscoverStateMachine(
        showsRepository: ShowsRepository,
        tmdbRepository: TmdbRepository,
    ): DiscoverStateMachine = DiscoverStateMachine(showsRepository, tmdbRepository)

}

