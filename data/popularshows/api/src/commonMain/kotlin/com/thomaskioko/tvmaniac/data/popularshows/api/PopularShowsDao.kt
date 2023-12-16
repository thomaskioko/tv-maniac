package com.thomaskioko.tvmaniac.data.popularshows.api

import com.thomaskioko.tvmaniac.core.db.PagedPopularShows
import com.thomaskioko.tvmaniac.core.db.Popular_shows
import kotlinx.coroutines.flow.Flow

interface PopularShowsDao {
    fun upsert(show: Popular_shows)
    fun upsert(list: List<Popular_shows>)
    fun observePopularShows(page: Long): Flow<List<PagedPopularShows>>
    fun deletePopularShow(id: Long)
    fun deletePopularShows()
}
