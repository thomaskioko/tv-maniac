package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.Tvshows

interface TvShowsDao {
    fun upsert(show: Tvshows)
    fun upsert(list: List<Tvshows>)
    fun deleteTvShows()
}
