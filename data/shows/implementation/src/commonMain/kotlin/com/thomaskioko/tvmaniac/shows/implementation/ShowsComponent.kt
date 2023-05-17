package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import me.tatarka.inject.annotations.Provides

interface ShowsComponent {

    @Provides
    fun provideShowsCache(bind: ShowDaoImpl): ShowsDao = bind

    @Provides
    fun provideShowsRepository(bind: ShowsRepositoryImpl): ShowsRepository = bind
}
