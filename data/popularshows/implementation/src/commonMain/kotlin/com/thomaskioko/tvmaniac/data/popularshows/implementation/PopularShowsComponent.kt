package com.thomaskioko.tvmaniac.data.popularshows.implementation

import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface PopularShowsComponent {

    @ApplicationScope
    @Provides
    fun provideTopRatedShowsDao(
        bind: DefaultPopularShowsDao,
    ): PopularShowsDao = bind

    @ApplicationScope
    @Provides
    fun provideTopRatedShowsRepository(
        bind: DefaultPopularShowsRepository,
    ): PopularShowsRepository = bind
}
