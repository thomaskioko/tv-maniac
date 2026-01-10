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
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultPopularShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : PopularShowsDao {
    private val popularShowsQueries = database.popularShowsQueries

    override fun upsert(show: Popular_shows) {
        popularShowsQueries.transaction {
            popularShowsQueries.insert(
                traktId = show.trakt_id,
                tmdbId = show.tmdb_id,
                page = show.page,
                name = show.name,
                poster_path = show.poster_path,
                overview = show.overview,
                page_order = show.page_order,
            )
        }
    }

    override fun observePopularShows(page: Long): Flow<List<ShowEntity>> =
        popularShowsQueries
            .entriesInPage(Id(page)) { traktId, tmdbId, pageId, name, posterPath, overview, inLibrary ->
                ShowEntity(
                    traktId = traktId.id,
                    tmdbId = tmdbId.id,
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
                ) { traktId, tmdbId, page, title, imageUrl, inLib ->
                    ShowEntity(
                        traktId = traktId.id,
                        tmdbId = tmdbId.id,
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
