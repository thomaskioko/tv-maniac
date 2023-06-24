package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface ShowsComponent {

    @ApplicationScope
    @Provides
    fun provideShowsCache(bind: ShowDaoImpl): ShowsDao = bind

    @ApplicationScope
    @Provides
    fun provideShowsRepository(bind: ShowsRepositoryImpl): ShowsRepository = bind
}
