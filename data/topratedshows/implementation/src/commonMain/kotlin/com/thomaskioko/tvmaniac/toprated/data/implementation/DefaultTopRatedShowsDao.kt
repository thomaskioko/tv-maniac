package com.thomaskioko.tvmaniac.toprated.data.implementation

import app.cash.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.paging3.QueryPagingSource
import com.thomaskioko.tvmaniac.core.db.TopRatedShows
import com.thomaskioko.tvmaniac.core.db.Toprated_shows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultTopRatedShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TopRatedShowsDao {
    private val topRatedShowsQueries = database.toprated_showsQueries

    override fun upsert(show: Toprated_shows) {
        topRatedShowsQueries.transaction {
            topRatedShowsQueries.insert(
                id = show.id,
                page = show.page,
            )
        }
    }

    override fun observeTopRatedShows(): Flow<List<TopRatedShows>> =
        topRatedShowsQueries.topRatedShows()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeTopRatedShows(page: Long): Flow<List<ShowEntity>> =
        topRatedShowsQueries.topRatedShowByPage(Id(page)) { id, page, title, imageUrl, inLib ->
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

    override fun getPagedTopRatedShows(): PagingSource<Int, ShowEntity> =
        QueryPagingSource(
            countQuery = topRatedShowsQueries.count(),
            transacter = topRatedShowsQueries,
            context = dispatchers.io,
            queryProvider = { limit, offset ->
                topRatedShowsQueries.pagedTopRatedShows(
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

    override fun getLastPage(): Long? =
        topRatedShowsQueries.getLastPage().executeAsOneOrNull()?.MAX?.id

    override fun deleteTrendingShows(id: Long) {
        topRatedShowsQueries.delete(Id(id))
    }

    override fun deleteTrendingShows() {
        topRatedShowsQueries.transaction {
            topRatedShowsQueries.deleteAll()
        }
    }
}
