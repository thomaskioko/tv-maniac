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
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUpcomingShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : UpcomingShowsDao {
    private val upcomingShowsQueries = database.upcomingShowsQueries

    override fun upsert(show: Upcoming_shows) {
        upcomingShowsQueries.transaction {
            upcomingShowsQueries.insert(
                id = show.id,
                page = show.page,
                name = show.name,
                poster_path = show.poster_path,
                overview = show.overview,
            )
        }
    }

    override fun observeUpcomingShows(page: Long): Flow<List<ShowEntity>> =
        upcomingShowsQueries
            .entriesInPage(Id(page)) { id, pageId, name, posterPath, overview, inLibrary ->
                ShowEntity(
                    id = id.id,
                    page = pageId.id,
                    title = name,
                    posterPath = posterPath,
                    overview = overview,
                    inLibrary = inLibrary == 1L,
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
