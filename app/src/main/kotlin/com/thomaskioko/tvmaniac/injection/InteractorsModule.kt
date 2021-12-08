package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepository
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepository
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.interactor.GetDiscoverShowListInteractor
import com.thomaskioko.tvmaniac.interactor.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.interactor.GetTrailersInteractor
import com.thomaskioko.tvmaniac.interactor.GetWatchListInteractor
import com.thomaskioko.tvmaniac.interactor.PopularShowsInteractor
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.interactor.UpdateWatchlistInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InteractorsModule {

    @Singleton
    @Provides
    fun providePopularShowsInteractor(
        repository: TvShowsRepository
    ): PopularShowsInteractor = PopularShowsInteractor(repository)

    @Singleton
    @Provides
    fun provideTvShowSeasonsInteractor(
        repository: SeasonsRepository
    ): SeasonsInteractor = SeasonsInteractor(repository)

    @Singleton
    @Provides
    fun provideEpisodesInteractor(
        repository: EpisodeRepository
    ): EpisodesInteractor = EpisodesInteractor(repository)

    @Singleton
    @Provides
    fun provideGetTrendingShowsInteractor(
        repository: TvShowsRepository
    ): GetDiscoverShowListInteractor = GetDiscoverShowListInteractor(repository)

    @Singleton
    @Provides
    fun provideGetShowInteractor(
        repository: TvShowsRepository
    ): GetShowInteractor = GetShowInteractor(repository)

    @Singleton
    @Provides
    fun provideGenreRepository(
        repository: GenreRepository
    ): GetGenresInteractor = GetGenresInteractor(repository)

    @Singleton
    @Provides
    fun provideGetTrailersInteractor(
        repository: TrailerRepository
    ): GetTrailersInteractor = GetTrailersInteractor(repository)

    @Singleton
    @Provides
    fun provideGetWatchListInteractor(
        repository: TvShowsRepository
    ): GetWatchListInteractor = GetWatchListInteractor(repository)

    @Singleton
    @Provides
    fun provideUpdateWatchlistInteractor(
        repository: TvShowsRepository
    ): UpdateWatchlistInteractor = UpdateWatchlistInteractor(repository)

    @Singleton
    @Provides
    fun provideGetShowsByTypeInteractor(
        repository: TvShowsRepository
    ): GetShowsByCategoryInteractor = GetShowsByCategoryInteractor(repository)
}
