package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.core.annotations.IoCoroutineScope
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.discover.implementation.repository.TvShowsRepositoryImpl
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.genre.api.GenreCache
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.genre.implementation.GenreRepositoryImpl
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import com.thomaskioko.tvmaniac.lastairepisodes.implementation.LastAirEpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesRepository
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.SeasonWithEpisodesRepositoryImpl
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsRepositoryImpl
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideTvShowsRepository(
        tvShowsService: TvShowsService,
        tvShowCache: TvShowCache,
        categoryCache: CategoryCache,
        showCategoryCache: ShowCategoryCache,
        epAirCacheLast: LastEpisodeAirCache,
        @IoCoroutineScope coroutineScope: CoroutineScope,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TvShowsRepository =
        TvShowsRepositoryImpl(
            apiService = tvShowsService,
            tvShowCache = tvShowCache,
            categoryCache = categoryCache,
            showCategoryCache = showCategoryCache,
            epAirCacheLast = epAirCacheLast,
            coroutineScope = coroutineScope,
            dispatcher = ioDispatcher
        )

    @Singleton
    @Provides
    fun provideGenreRepository(
        tvShowsService: TvShowsService,
        cache: GenreCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): GenreRepository = GenreRepositoryImpl(tvShowsService, cache, ioDispatcher)

    @Singleton
    @Provides
    fun provideTvShowSeasonsRepository(
        tvShowsService: TvShowsService,
        seasonCache: SeasonsCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonsRepository = SeasonsRepositoryImpl(
        tvShowsService,
        seasonCache,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideEpisodeRepository(
        tvShowsService: TvShowsService,
        episodesCache: EpisodesCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): EpisodeRepository = EpisodeRepositoryImpl(
        tvShowsService,
        episodesCache,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideLastAirEpisodeRepository(
        cache: LastEpisodeAirCache,
    ): LastAirEpisodeRepository = LastAirEpisodeRepositoryImpl(cache)

    @Singleton
    @Provides
    fun provideRelatedShowsRepository(
        tvShowsService: TvShowsService,
        similarShowCache: SimilarShowCache,
        tvShowCache: TvShowCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SimilarShowsRepository =
        SimilarShowsRepositoryImpl(
            apiService = tvShowsService,
            similarShowCache = similarShowCache,
            tvShowCache = tvShowCache,
            dispatcher = ioDispatcher
        )

    @Singleton
    @Provides
    fun provideSeasonWithEpisodesRepository(
        apiService: TvShowsService,
        episodesCache: EpisodesCache,
        seasonWithEpisodesCache: SeasonWithEpisodesCache,
        seasonsCache: SeasonsCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonWithEpisodesRepository = SeasonWithEpisodesRepositoryImpl(
        apiService,
        episodesCache,
        seasonsCache,
        seasonWithEpisodesCache,
        ioDispatcher
    )
}
