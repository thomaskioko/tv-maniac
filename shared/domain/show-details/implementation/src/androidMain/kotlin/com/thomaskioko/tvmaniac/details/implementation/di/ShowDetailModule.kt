package com.thomaskioko.tvmaniac.details.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.details.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveFollowingInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.UpdateFollowingInteractor
import com.thomaskioko.tvmaniac.details.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.details.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.details.implementation.repository.TmdbRepositoryImpl
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.shared.core.ui.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShowDetailModule {

    @Singleton
    @Provides
    fun provideTvShowCache(database: TvManiacDatabase): TvShowCache {
        return TvShowCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideShowCategoryCache(database: TvManiacDatabase): ShowCategoryCache {
        return ShowCategoryCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideTvShowsRepository(
        tmdbService: TmdbService,
        tvShowCache: TvShowCache,
        similarShowCache: SimilarShowCache,
        showCategoryCache: ShowCategoryCache,
        epAirCacheLast: LastEpisodeAirCache,
        @IoCoroutineScope coroutineScope: CoroutineScope,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TmdbRepository =
        TmdbRepositoryImpl(
            apiService = tmdbService,
            tvShowCache = tvShowCache,
            showCategoryCache = showCategoryCache,
            epAirCacheLast = epAirCacheLast,
            similarShowCache = similarShowCache,
            coroutineScope = coroutineScope,
            dispatcher = ioDispatcher
        )

    @Singleton
    @Provides
    fun provideObserveShowInteractor(
        traktRepository: TraktRepository,
        similarShowsRepository: SimilarShowsRepository,
        seasonsRepository: SeasonsRepository,
        genreRepository: GenreRepository,
        lastAirRepository: LastAirEpisodeRepository,
        trailerRepository: TrailerRepository
    ): ObserveShowInteractor = ObserveShowInteractor(
        traktRepository,
        similarShowsRepository,
        seasonsRepository,
        genreRepository,
        lastAirRepository,
        trailerRepository
    )

    @Singleton
    @Provides
    fun provideUpdateFollowingInteractor(
        traktRepository: TraktRepository
    ): UpdateFollowingInteractor = UpdateFollowingInteractor(traktRepository)

    @Singleton
    @Provides
    fun provideObserveWatchListInteractor(
        repository: TraktRepository
    ): ObserveFollowingInteractor = ObserveFollowingInteractor(repository)

    @Singleton
    @Provides
    fun provideGetShowsByTypeInteractor(
        repository: TmdbRepository
    ): ObserveShowsByCategoryInteractor = ObserveShowsByCategoryInteractor(repository)
}
