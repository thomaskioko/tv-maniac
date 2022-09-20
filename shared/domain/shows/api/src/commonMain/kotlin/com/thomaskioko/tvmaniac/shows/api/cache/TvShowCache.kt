package com.thomaskioko.tvmaniac.shows.api.cache

import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import kotlinx.coroutines.flow.Flow

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun updateShow(
        tmdbId: Int,
        posterUrl: String?,
        backdropUrl: String?
    )

    fun observeTvShow(showId: Int): Flow<Show?>

    fun observeTvShows(): Flow<List<Show>>

    fun observeShowAirEpisodes(showId: Int): Flow<List<AirEpisodesByShowId>>

    fun observeShowsByCategoryID(categoryId: Int): Flow<List<SelectShowsByCategory>>

    fun getTvShow(traktId: Int): Show?

    fun getTvShowByTmdbId(tmdbId: Int?): Show?

    fun observeTvShowsArt(): Flow<List<Show>>

    fun deleteTvShows()

    fun getShowsByCategoryID(categoryId: Int): List<SelectShowsByCategory>
}
