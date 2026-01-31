package com.thomaskioko.tvmaniac.data.library.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.library.LibraryDao
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.LibraryShows
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.WatchProvidersForShow
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultLibraryDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : LibraryDao {

    override fun observeLibrary(
        sortOption: LibrarySortOption,
        followedOnly: Boolean,
    ): Flow<List<LibraryShows>> =
        database.libraryQueries.libraryShows(
            query = null,
            followedOnly = if (followedOnly) 1L else 0L,
            sortOption = sortOption.name,
        )
            .asFlow()
            .mapToList(dispatchers.io)

    override fun searchLibrary(query: String): Flow<List<LibraryShows>> =
        database.libraryQueries.libraryShows(
            query = query,
            followedOnly = 0L,
            sortOption = LibrarySortOption.LAST_WATCHED.name,
        )
            .asFlow()
            .mapToList(dispatchers.io)

    override fun getWatchProviders(tmdbId: Long): List<WatchProvidersForShow> =
        database.libraryQueries.watchProvidersForShow(Id<TmdbId>(tmdbId)).executeAsList()
}
