package com.thomaskioko.tvmaniac.data.popularshows.implementation

import androidx.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.paging.QueryPagingSource
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Popular_shows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultPopularShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : PopularShowsDao {
    private val popularShowsQueries = database.popularShowsQueries

    override fun upsert(show: Popular_shows) {
        popularShowsQueries.transaction {
            popularShowsQueries.insert(
                id = show.id,
                page = show.page,
                name = show.name,
                poster_path = show.poster_path,
                overview = show.overview,
            )
        }
    }

    override fun observePopularShows(page: Long): Flow<List<ShowEntity>> =
        popularShowsQueries
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
