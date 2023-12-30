package com.thomaskioko.tvmaniac.data.popularshows.api

import com.thomaskioko.tvmaniac.core.db.Popular_shows
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface PopularShowsDao {
    fun upsert(show: Popular_shows)
    fun upsert(list: List<Popular_shows>)
    fun observePopularShows(page: Long): Flow<List<ShowEntity>>
    fun deletePopularShow(id: Long)
    fun deletePopularShows()
}
