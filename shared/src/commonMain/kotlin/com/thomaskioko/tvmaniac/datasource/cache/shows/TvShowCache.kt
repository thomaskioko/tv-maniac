package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun getTvShow(showId: Int): Show

    fun getTvShows(): List<Show>

    fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Show>

    fun getFeaturedTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Show>

    fun updateShowDetails(showId: Int, showStatus: String, seasonIds: List<Int>)

    fun deleteTvShows()
}