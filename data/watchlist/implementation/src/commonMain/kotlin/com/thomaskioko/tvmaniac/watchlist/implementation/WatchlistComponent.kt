package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface WatchlistComponent {

    @ApplicationScope
    @Provides
    fun provideWatchlistDao(bind: WatchlistDaoImpl): WatchlistDao = bind

    @ApplicationScope
    @Provides
    fun provideWatchlist(bind: WatchlistRepositoryImpl): WatchlistRepository = bind
}
