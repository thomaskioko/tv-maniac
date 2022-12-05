package com.thomaskioko.tvmaniac.show_grid.di

import com.thomaskioko.tvmaniac.show_grid.ObservePagedShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InteractorModule {

    @Singleton
    @Provides
    fun provideObservePagedShowsByCategoryInteractor(
        repository: TraktRepository
    ): ObservePagedShowsByCategoryInteractor = ObservePagedShowsByCategoryInteractor(repository)

}
