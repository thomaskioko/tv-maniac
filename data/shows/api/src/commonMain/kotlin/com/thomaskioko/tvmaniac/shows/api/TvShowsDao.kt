package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.db.Tvshows
import kotlinx.coroutines.flow.Flow

interface TvShowsDao {
    fun upsert(show: Tvshows)
    fun upsert(list: List<Tvshows>)
    fun observeTvShows(id: Long): Flow<TvshowDetails>
    fun getTvShow(id: Long): TvshowDetails
    fun deleteTvShow(id: Long)
    fun deleteTvShows()
}
