package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface DiscoverComponent {

    @ApplicationScope
    @Provides
    fun provideShowsCache(bind: ShowDaoImpl): ShowsDao = bind

    @ApplicationScope
    @Provides
    fun provideTvShowsDao(
        bind: DefaultTvShowsDao,
    ): TvShowsDao = bind

    @ApplicationScope
    @Provides
    fun provideDiscoverRepository(bind: DiscoverRepositoryImpl): DiscoverRepository = bind
}
