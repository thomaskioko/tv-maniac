package com.thomaskioko.tvmaniac.data.popularshows.implementation

import androidx.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Popular_shows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.paging.QueryPagingSource
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultPopularShowsDao(
  database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : PopularShowsDao {
  private val popularShowsQueries = database.popular_showsQueries

  override fun upsert(show: Popular_shows) {
    popularShowsQueries.transaction {
      popularShowsQueries.insert(
        id = show.id,
        page = show.page,
      )
    }
  }

  override fun observePopularShows(page: Long): Flow<List<ShowEntity>> =
    popularShowsQueries
      .popularShows { id, pageId, title, imageUrl, inLib ->
        ShowEntity(
          id = id.id,
          page = pageId.id,
          title = title,
          posterPath = imageUrl,
          inLibrary = inLib == 1L,
        )
      }
      .asFlow()
      .mapToList(dispatchers.io)

  override fun getPagedPopularShows(): PagingSource<Int, ShowEntity> =
    QueryPagingSource(
      countQuery = popularShowsQueries.count(),
      transacter = popularShowsQueries,
      context = dispatchers.io,
      queryProvider = { limit, offset ->
        popularShowsQueries.pagedPopularShows(
          limit = limit,
          offset = offset,
        ) { id, page, title, imageUrl, inLib ->
          ShowEntity(
            id = id.id,
            page = page.id,
            title = title,
            posterPath = imageUrl,
            inLibrary = inLib == 1L,
          )
        }
      },
    )

  override fun deletePopularShow(id: Long) {
    popularShowsQueries.delete(Id(id))
  }

  override fun deletePopularShows() {
    popularShowsQueries.transaction { popularShowsQueries.deleteAll() }
  }

  override fun pageExists(page: Long): Boolean {
    return popularShowsQueries.pageExists(Id(page)).executeAsOne()
  }
}
