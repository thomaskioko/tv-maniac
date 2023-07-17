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
): DataLoaded = DataLoaded(
    trendingShows = trending.requireData().toTvShowList(),
    popularShows = popular.requireData().toTvShowList(),
    anticipatedShows = anticipated.requireData().toTvShowList(),
    recommendedShows = recommended.requireData().take(5).toTvShowList(),
    errorMessage = getErrorMessage(trending, popular, anticipated, recommended),
    isContentEmpty = trending.dataOrNull().isNullOrEmpty() &&
        popular.dataOrNull().isNullOrEmpty() && anticipated.dataOrNull().isNullOrEmpty() &&
        recommended.dataOrNull().isNullOrEmpty(),
)

private fun getErrorMessage(
    trending: StoreReadResponse<List<ShowsByCategory>>,
    popular: StoreReadResponse<List<ShowsByCategory>>,
    anticipated: StoreReadResponse<List<ShowsByCategory>>,
    recommended: StoreReadResponse<List<ShowsByCategory>>,
) = trending.errorMessageOrNull() ?: popular.errorMessageOrNull()
    ?: anticipated.errorMessageOrNull() ?: recommended.errorMessageOrNull()
