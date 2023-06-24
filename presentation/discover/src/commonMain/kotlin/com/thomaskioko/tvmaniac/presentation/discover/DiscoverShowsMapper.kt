package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import org.mobilenativefoundation.store.store5.StoreReadResponse

fun List<ShowsByCategory>.toTvShowList(): List<TvShow> = map { it.toTvShow() }

fun ShowsByCategory.toTvShow(): TvShow = TvShow(
    traktId = trakt_id,
    tmdbId = tmdb_id,
    title = title,
    overview = overview,
    language = language,
    posterImageUrl = poster_url,
    backdropImageUrl = backdrop_url,
    votes = votes,
    rating = rating,
    genres = genres,
    year = year,
    status = status,
)

fun toShowResultState(
    trending: StoreReadResponse<List<ShowsByCategory>>,
    popular: StoreReadResponse<List<ShowsByCategory>>,
    anticipated: StoreReadResponse<List<ShowsByCategory>>,
    recommended: StoreReadResponse<List<ShowsByCategory>>,
): DiscoverContent.DiscoverContentState {
    val trendingShows = if (trending is StoreReadResponse.Data) {
        trending.requireData()
            .toTvShowList()
    } else {
        emptyList()
    }
    val popularShows = if (popular is StoreReadResponse.Data) {
        popular.requireData()
            .toTvShowList()
    } else {
        emptyList()
    }
    val anticipatedShows = if (anticipated is StoreReadResponse.Data) {
        anticipated.requireData()
            .toTvShowList()
    } else {
        emptyList()
    }
    val recommendedShows = if (recommended is StoreReadResponse.Data) {
        recommended.requireData()
            .toTvShowList()
    } else {
        emptyList()
    }

    return if (trendingShows.isEmpty() && popularShows.isEmpty() &&
        anticipatedShows.isEmpty() && recommendedShows.isEmpty()
    ) {
        DiscoverContent.EmptyResult
    } else {
        DiscoverContent.DataLoaded(
            trendingShows = trendingShows,
            popularShows = popularShows,
            anticipatedShows = anticipatedShows,
            recommendedShows = recommendedShows.take(5),
        )
    }
}
