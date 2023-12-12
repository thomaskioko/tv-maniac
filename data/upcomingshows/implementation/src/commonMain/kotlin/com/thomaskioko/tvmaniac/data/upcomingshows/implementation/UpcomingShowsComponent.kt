package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface UpcomingShowsComponent {

    @ApplicationScope
    @Provides
    fun provideUpcomingShowsDao(
        bind: DefaultUpcomingShowsDao,
    ): UpcomingShowsDao = bind

    @ApplicationScope
    @Provides
    fun provideUpcomingShowsRepository(
        bind: DefaultUpcomingShowsRepository,
    ): UpcomingShowsRepository = bind
}
