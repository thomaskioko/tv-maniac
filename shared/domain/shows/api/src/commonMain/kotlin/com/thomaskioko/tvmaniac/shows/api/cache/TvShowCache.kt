package com.thomaskioko.tvmaniac.shows.api.cache

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import kotlinx.coroutines.flow.Flow

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun observeTvShow(showId: Int): Flow<SelectByShowId?>

    fun observeTvShows(): Flow<List<SelectShows>>

    fun observeShowsByCategoryID(categoryId: Int): Flow<List<SelectShowsByCategory>>

    fun getTvShow(traktId: Int): SelectByShowId?

    fun getTvShowByTmdbId(tmdbId: Int?): Show?

    fun deleteTvShows()

    fun getShowsByCategoryID(categoryId: Int): List<SelectShowsByCategory>
}
