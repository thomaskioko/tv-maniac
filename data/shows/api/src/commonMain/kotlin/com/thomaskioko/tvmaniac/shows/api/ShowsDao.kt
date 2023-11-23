package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.Shows
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import kotlinx.coroutines.flow.Flow

interface ShowsDao {

    fun upsert(show: Show)

    fun upsert(list: List<Show>)

    fun observeTvShow(showId: Long): Flow<ShowById>

    fun observeCachedShows(categoryId: Long): Flow<List<ShowsByCategory>>

    fun observeShows(): Flow<List<Shows>>

    fun getTvShow(traktId: Long): ShowById

    fun deleteTvShows()
}
