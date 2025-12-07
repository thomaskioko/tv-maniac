package com.thomaskioko.tvmaniac.toprated.data.implementation

import androidx.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.paging.QueryPagingSource
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TopRatedShows
import com.thomaskioko.tvmaniac.db.Toprated_shows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTopRatedShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TopRatedShowsDao {
    private val topRatedShowsQueries = database.topratedShowsQueries

    override fun upsert(show: Toprated_shows) {
        topRatedShowsQueries.transaction {
            topRatedShowsQueries.insert(
                id = show.id,
                page = show.page,
                name = show.name,
                poster_path = show.poster_path,
                overview = show.overview,
                page_order = show.page_order,
            )
        }
    }

    override fun observeTopRatedShows(): Flow<List<TopRatedShows>> =
        topRatedShowsQueries.topRatedShows().asFlow().mapToList(dispatchers.io)

    override fun observeTopRatedShows(page: Long): Flow<List<ShowEntity>> =
        topRatedShowsQueries
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

    override fun pageExists(page: Long): Boolean {
        return topRatedShowsQueries.pageExists(Id(page)).executeAsOne()
    }

    override fun deleteTrendingShows(id: Long) {
        topRatedShowsQueries.delete(Id(id))
    }

    override fun deleteTrendingShows() {
        topRatedShowsQueries.transaction { topRatedShowsQueries.deleteAll() }
    }
}
