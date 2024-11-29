package com.thomaskioko.tvmaniac.watchlist.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.Library
import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.LibraryDao
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class DefaultLibraryDao(
  private val database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : LibraryDao {

  override fun upsert(watchlist: Library) {
    database.transaction {
      database.libraryQueries.upsert(
        id = watchlist.id,
        created_at = watchlist.created_at,
      )
    }
  }

  override fun upsert(watchedShowList: List<Library>) {
    watchedShowList.forEach { upsert(it) }
  }

  override fun getShowsInLibrary(): List<LibraryShows> =
    database.libraryQueries.libraryShows().executeAsList()

  override fun observeShowsInLibrary(): Flow<List<LibraryShows>> =
    database.libraryQueries.libraryShows().asFlow().mapToList(dispatchers.io)

  override fun delete(traktId: Long) {
    database.libraryQueries.delete(Id(traktId))
  }
}
