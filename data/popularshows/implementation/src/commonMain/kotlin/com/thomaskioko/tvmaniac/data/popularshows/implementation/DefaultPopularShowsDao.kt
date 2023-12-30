package com.thomaskioko.tvmaniac.data.popularshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Popular_shows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
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

    override fun upsert(list: List<Popular_shows>) {
        list.forEach { upsert(it) }
    }

    override fun observePopularShows(page: Long): Flow<List<ShowEntity>> =
        popularShowsQueries.popularShows(Id(page)) { id, page, title, imageUrl, inLib ->
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

    override fun deletePopularShow(id: Long) {
        popularShowsQueries.delete(Id(id))
    }

    override fun deletePopularShows() {
        popularShowsQueries.transaction {
            popularShowsQueries.deleteAll()
        }
    }
}
