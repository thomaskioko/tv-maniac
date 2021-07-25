package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.GetTrendingShowsInteractor
import com.thomaskioko.tvmaniac.interactor.PopularShowsInteractor
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
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
    ) : PopularShowsInteractor = PopularShowsInteractor(repository)

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
    ): GetTrendingShowsInteractor = GetTrendingShowsInteractor(repository)

    @Singleton
    @Provides
    fun provideGetShowInteractor(
        repository: TvShowsRepository
    ): GetShowInteractor = GetShowInteractor(repository)
}