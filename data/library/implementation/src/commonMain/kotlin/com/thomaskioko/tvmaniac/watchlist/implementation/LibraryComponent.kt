package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.shows.api.LibraryDao
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface LibraryComponent {

    @ApplicationScope
    @Provides
    fun provideWatchlistDao(bind: LibraryDaoImpl): LibraryDao = bind

    @ApplicationScope
    @Provides
    fun provideWatchlist(bind: LibraryRepositoryImpl): LibraryRepository = bind
}
