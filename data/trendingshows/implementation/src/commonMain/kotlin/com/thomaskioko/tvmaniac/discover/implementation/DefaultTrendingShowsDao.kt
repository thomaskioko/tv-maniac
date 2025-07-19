package com.thomaskioko.tvmaniac.discover.implementation

import androidx.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.paging.QueryPagingSource
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Trending_shows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTrendingShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TrendingShowsDao {

    private val trendingShowsQueries = database.trendingShowsQueries

    override fun upsert(show: Trending_shows) {
        trendingShowsQueries.transaction {
            trendingShowsQueries.insert(
                id = show.id,
                page = show.page,
            )
        }
    }

    override fun observeTvShow(page: Long): Flow<List<ShowEntity>> =
        trendingShowsQueries
            .trendingShowsByPage(Id(page)) { id, page, title, imageUrl, inLib ->
                ShowEntity(
                    id = id.id,
                    page = page.id,
                    title = title,
                    posterPath = imageUrl,
                    inLibrary = inLib == 1L,
                )
            }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun getPagedTrendingShows(): PagingSource<Int, ShowEntity> =
        QueryPagingSource(
            countQuery = trendingShowsQueries.count(),
            transacter = trendingShowsQueries,
            context = dispatchers.io,
            queryProvider = { limit, offset ->
                trendingShowsQueries.pagedTrendingShows(
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

    override fun pageExists(page: Long): Boolean {
        return trendingShowsQueries.pageExists(Id(page)).executeAsOne()
    }

    override fun deleteTrendingShow(id: Long) {
        trendingShowsQueries.delete(Id(id))
    }

    override fun deleteTrendingShows() {
        trendingShowsQueries.transaction { trendingShowsQueries.deleteAll() }
    }
}
