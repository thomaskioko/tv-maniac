package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import me.tatarka.inject.annotations.Provides

interface WatchlistComponent {

    @Provides
    fun provideWatchlistDao(bind: WatchlistDaoImpl): WatchlistDao = bind

    @Provides
    fun provideWatchlist(bind: WatchlistRepositoryImpl): WatchlistRepository = bind
}
