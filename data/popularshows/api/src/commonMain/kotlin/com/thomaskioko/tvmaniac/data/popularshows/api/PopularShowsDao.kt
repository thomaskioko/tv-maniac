package com.thomaskioko.tvmaniac.data.popularshows.api

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.db.Popular_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface PopularShowsDao {
    public fun upsert(show: Popular_shows)

    public fun observePopularShows(page: Long): Flow<List<ShowEntity>>

    public fun getPagedPopularShows(): PagingSource<Int, ShowEntity>

    public fun deletePopularShow(id: Long)

    public fun deletePopularShows()

    public fun pageExists(page: Long): Boolean
}
