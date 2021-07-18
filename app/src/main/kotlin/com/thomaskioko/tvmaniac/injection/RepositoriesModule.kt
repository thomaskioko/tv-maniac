package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideTvShowsRepository(
        tvShowsService: TvShowsService,
        cache: TvShowCache
    ): TvShowsRepository = TvShowsRepositoryImpl(tvShowsService, cache)

    @Singleton
    @Provides
    fun provideTvShowSeasonsRepository(
        tvShowsService: TvShowsService,
        cache: TvShowCache,
        seasonCache: SeasonsCache
    ): SeasonsRepository = SeasonsRepositoryImpl(
        tvShowsService,
        cache,
        seasonCache
    )

    @Singleton
    @Provides
    fun provideEpisodeRepository(
        tvShowsService: TvShowsService,
        episodesCache: EpisodesCache,
        seasonCache: SeasonsCache
    ): EpisodeRepository = EpisodeRepositoryImpl(
        tvShowsService,
        episodesCache,
        seasonCache
    )
}