package com.thomaskioko.tvmaniac.details.implementation.di

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.details.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveFollowingInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.UpdateFollowingInteractor
import com.thomaskioko.tvmaniac.showcommon.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.details.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.details.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.details.implementation.repository.TvShowsRepositoryImpl
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.shared.core.ui.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
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
        tvShowsService: TvShowsService,
        tvShowCache: TvShowCache,
        showCategoryCache: ShowCategoryCache,
        epAirCacheLast: LastEpisodeAirCache,
        @IoCoroutineScope coroutineScope: CoroutineScope,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TvShowsRepository =
        TvShowsRepositoryImpl(
            apiService = tvShowsService,
            tvShowCache = tvShowCache,
            showCategoryCache = showCategoryCache,
            epAirCacheLast = epAirCacheLast,
            coroutineScope = coroutineScope,
            dispatcher = ioDispatcher
        )

    @Singleton
    @Provides
    fun provideObserveShowInteractor(
        showsRepository: TvShowsRepository,
        similarShowsRepository: SimilarShowsRepository,
        seasonsRepository: SeasonsRepository,
        genreRepository: GenreRepository,
        lastAirRepository: LastAirEpisodeRepository,
        trailerRepository: TrailerRepository
    ): ObserveShowInteractor = ObserveShowInteractor(
        showsRepository,
        similarShowsRepository,
        seasonsRepository,
        genreRepository,
        lastAirRepository,
        trailerRepository
    )

    @Singleton
    @Provides
    fun provideUpdateFollowingInteractor(
        repository: TvShowsRepository
    ): UpdateFollowingInteractor = UpdateFollowingInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveWatchListInteractor(
        repository: TvShowsRepository
    ): ObserveFollowingInteractor = ObserveFollowingInteractor(repository)

    @Singleton
    @Provides
    fun provideGetShowsByTypeInteractor(
        repository: TvShowsRepository
    ): ObserveShowsByCategoryInteractor = ObserveShowsByCategoryInteractor(repository)
}
