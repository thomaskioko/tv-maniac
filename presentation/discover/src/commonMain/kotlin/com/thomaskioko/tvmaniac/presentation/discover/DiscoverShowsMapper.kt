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
): DiscoverContent {
    return DiscoverContent(
        trendingShows = trending.requireData().toTvShowList(),
        popularShows = popular.requireData().toTvShowList(),
        anticipatedShows = anticipated.requireData().toTvShowList(),
        recommendedShows = recommended.requireData().toTvShowList().take(5),
    )
}
