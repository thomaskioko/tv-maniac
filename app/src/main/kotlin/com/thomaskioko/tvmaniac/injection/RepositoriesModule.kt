package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.core.annotations.IoCoroutineScope
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.cache.trailers.TrailerCache
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepository
import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepository
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideTvShowsRepository(
        tvShowsService: TvShowsService,
        cache: TvShowCache,
        @IoCoroutineScope coroutineScope: CoroutineScope
    ): TvShowsRepository = TvShowsRepositoryImpl(tvShowsService, cache, coroutineScope)

    @Singleton
    @Provides
    fun provideGenreRepository(
        tvShowsService: TvShowsService,
        cache: GenreCache
    ): GenreRepository = GenreRepositoryImpl(tvShowsService, cache)

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

    @Singleton
    @Provides
    fun provideTrailerRepository(
        tvShowsService: TvShowsService,
        cache: TrailerCache
    ): TrailerRepository = TrailerRepositoryImpl(tvShowsService, cache)
}
