package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.Shows
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import kotlinx.coroutines.flow.Flow

interface ShowsDao {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun observeTvShow(showId: Long): Flow<ShowById>

    fun observeCachedShows(categoryId: Long): Flow<List<ShowsByCategory>>

    fun observeShows(): Flow<List<Shows>>

    fun getTvShow(traktId: Long): ShowById

    fun deleteTvShows()

    fun getShowsByCategoryID(categoryId: Long): List<ShowsByCategory>
}
