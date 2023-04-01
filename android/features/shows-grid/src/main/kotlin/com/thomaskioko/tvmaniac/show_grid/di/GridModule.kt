package com.thomaskioko.tvmaniac.show_grid.di

import com.thomaskioko.tvmaniac.show_grid.GridStateMachine
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GridModule {

    @Singleton
    @Provides
    fun provideGridStateMachine(
        repository: ShowsRepository
    ): GridStateMachine = GridStateMachine(repository)

}
