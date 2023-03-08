package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module
import javax.inject.Singleton

actual fun seasonDetailsDataModule() : org.koin.core.module.Module = module {  }

@Module
@InstallIn(SingletonComponent::class)
object SeasonDetailsModule {

    @Singleton
    @Provides
    fun provideSeasonDetailsRepository(
        traktService: TraktService,
        seasonCache: SeasonsCache,
        episodesCache: EpisodesCache,
        datastoreRepository: DatastoreRepository,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonDetailsRepository = SeasonDetailsRepositoryImpl(
        traktService,
        seasonCache,
        episodesCache,
        datastoreRepository,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideTvShowSeasonCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonsCache = SeasonsCacheImpl(database, ioDispatcher)

}
