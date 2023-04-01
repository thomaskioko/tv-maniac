package com.thomaskioko.tvmaniac.shows.api.cache

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShowImages
import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import kotlinx.coroutines.flow.Flow

interface ShowsCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun observeTvShow(showId: Long): Flow<SelectByShowId>

    fun observeTvShows(): Flow<List<SelectShows>>

    fun observeCachedShows(categoryId: Long): Flow<List<SelectShowsByCategory>>

    fun observeShowImages(): Flow<List<SelectShowImages>>

    fun getTvShow(traktId: Long): SelectByShowId

    fun deleteTvShows()

    fun getShowsByCategoryID(categoryId: Long): List<SelectShowsByCategory>
}
