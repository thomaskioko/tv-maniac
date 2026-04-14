package com.thomaskioko.tvmaniac.data.library.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.library.LibraryDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.LibraryShows
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.WatchProvidersForShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultLibraryDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : LibraryDao {

    override fun observeLibrary(followedOnly: Boolean): Flow<List<LibraryShows>> =
        database.libraryQueries.libraryShows(
            query = null,
            followedOnly = if (followedOnly) 1L else 0L,
        )
            .asFlow()
            .mapToList(dispatchers.io)

    override fun searchLibrary(query: String): Flow<List<LibraryShows>> =
        database.libraryQueries.libraryShows(
            query = query,
            followedOnly = 0L,
        )
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeWatchProviders(tmdbId: Long): Flow<List<WatchProvidersForShow>> =
        database.libraryQueries.watchProvidersForShow(Id<TmdbId>(tmdbId))
            .asFlow()
            .mapToList(dispatchers.databaseRead)
}
