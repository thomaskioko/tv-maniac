package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.datasource.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.interactor.PopularShowsInteractor
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
}