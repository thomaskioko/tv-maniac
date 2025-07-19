package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import androidx.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.paging.QueryPagingSource
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Upcoming_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultUpcomingShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : UpcomingShowsDao {
    private val upcomingShowsQueries = database.upcomingShowsQueries

    override fun upsert(show: Upcoming_shows) {
        upcomingShowsQueries.transaction {
            upcomingShowsQueries.insert(
                id = show.id,
                page = show.page,
            )
        }
    }

    override fun observeUpcomingShows(page: Long): Flow<List<ShowEntity>> =
        upcomingShowsQueries
            .upcomingShowsByPage(Id(page)) { id, page, title, imageUrl, inLib ->
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

    override fun getPagedUpcomingShows(): PagingSource<Int, ShowEntity> =
        QueryPagingSource(
            countQuery = upcomingShowsQueries.count(),
            transacter = upcomingShowsQueries,
            context = dispatchers.io,
            queryProvider = { limit, offset ->
                upcomingShowsQueries.pagedUpcomingShows(
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
        return upcomingShowsQueries.pageExists(Id(page)).executeAsOne()
    }

    override fun deleteUpcomingShow(id: Long) {
        upcomingShowsQueries.delete(Id(id))
    }

    override fun deleteUpcomingShows() {
        upcomingShowsQueries.transaction { upcomingShowsQueries.deleteAll() }
    }
}
