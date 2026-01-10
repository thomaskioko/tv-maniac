package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse

/**
 * Remote data source for fetching TV show data from the Trakt API.
 *
 * @see [Trakt API Documentation](https://trakt.docs.apiary.io/)
 */
public interface TraktShowsRemoteDataSource {

    /**
     * Fetches currently trending shows.
     *
     * Trending shows are calculated based on the most watches, collected, and favorited
     * activities over the last 24 hours.
     *
     * @param page Page number for pagination (1-indexed)
     * @param limit Number of results per page (max 100)
     * @return List of shows with engagement metrics (watchers count)
     * @see [Trakt Trending Shows](https://trakt.docs.apiary.io/#reference/shows/trending)
     */
    public suspend fun getTrendingShows(
        page: Int = 1,
        limit: Int = 20,
    ): ApiResponse<List<TraktShowsResponse>>

    /**
     * Fetches popular shows.
     *
     * Popularity is calculated based on total number of favorites, plays, and watchers.
     *
     * @param page Page number for pagination (1-indexed)
     * @param limit Number of results per page (max 100)
     * @return List of popular shows ordered by popularity score
     * @see [Trakt Popular Shows](https://trakt.docs.apiary.io/#reference/shows/popular)
     */
    public suspend fun getPopularShows(
        page: Int = 1,
        limit: Int = 20,
    ): ApiResponse<List<TraktShowResponse>>

    /**
     * Fetches most favorited shows within a time period.
     *
     * Used for "Top Rated" displays. Shows are ranked by favorite count within the period.
     *
     * @param page Page number for pagination (1-indexed)
     * @param limit Number of results per page (max 100)
     * @param period Time window for calculating favorites
     * @return List of shows with engagement metrics (user count)
     * @see [Trakt Favorited Shows](https://trakt.docs.apiary.io/#reference/shows/favorited)
     */
    public suspend fun getFavoritedShows(
        page: Int = 1,
        limit: Int = 20,
        period: TimePeriod = TimePeriod.WEEKLY,
    ): ApiResponse<List<TraktShowsResponse>>

    /**
     * Fetches most watched shows within a time period.
     *
     * Shows are ranked by total plays (episode watches) within the period.
     *
     * @param page Page number for pagination (1-indexed)
     * @param limit Number of results per page (max 100)
     * @param period Time window for calculating watch counts
     * @return List of shows with engagement metrics (play count)
     * @see [Trakt Watched Shows](https://trakt.docs.apiary.io/#reference/shows/watched)
     */
    public suspend fun getMostWatchedShows(
        page: Int = 1,
        limit: Int = 20,
        period: TimePeriod = TimePeriod.WEEKLY,
    ): ApiResponse<List<TraktShowsResponse>>

    /**
     * Fetches shows related to a specific show.
     *
     * Related shows are determined by Trakt's recommendation algorithm based on
     * user behavior patterns (users who liked X also liked Y).
     *
     * @param traktId The Trakt ID of the show to find related shows for
     * @param page Page number for pagination (1-indexed)
     * @param limit Number of results per page (max 100)
     * @return List of related shows
     * @see [Trakt Related Shows](https://trakt.docs.apiary.io/#reference/shows/related)
     */
    public suspend fun getRelatedShows(
        traktId: Long,
        page: Int = 1,
        limit: Int = 20,
    ): ApiResponse<List<TraktShowResponse>>

    /**
     * Fetches detailed information for a specific show.
     *
     * @param traktId The Trakt ID of the show
     * @return Full show details including all extended information
     * @see [Trakt Show Summary](https://trakt.docs.apiary.io/#reference/shows/summary)
     */
    public suspend fun getShowDetails(traktId: Long): ApiResponse<TraktShowResponse>

    /**
     * Fetches all seasons for a specific show with extended information.
     *
     * @param traktId The Trakt ID of the show
     * @return List of seasons with episode counts, ratings, and air dates
     * @see [Trakt Show Seasons](https://trakt.docs.apiary.io/#reference/seasons/summary)
     */
    public suspend fun getShowSeasons(traktId: Long): ApiResponse<List<TraktSeasonsResponse>>

    /**
     * Fetches a specific season with all episodes.
     *
     * @param traktId The Trakt ID of the show
     * @param seasonNumber The season number to fetch
     * @return Season details with all episodes
     * @see [Trakt Season Summary](https://trakt.docs.apiary.io/#reference/seasons/season)
     */
    public suspend fun getSeasonEpisodes(
        traktId: Long,
        seasonNumber: Int,
    ): ApiResponse<List<TraktEpisodesResponse>>

    /**
     * Searches for a show by its TMDB ID.
     *
     * Used to cross-reference shows between TMDB and Trakt APIs. Returns search results
     * that may include movies - filter by `type == "show"` to get the show entry.
     *
     * @param tmdbId The TMDB ID to search for
     * @return List of search results. Extract the show with:
     *         `results.firstOrNull { it.type == "show" }?.show`
     * @see [Trakt ID Lookup](https://trakt.docs.apiary.io/#reference/search/id-lookup)
     */
    public suspend fun getShowByTmdbId(tmdbId: Long): ApiResponse<List<TraktSearchResult>>
}

/**
 * Time periods for filtering Trakt statistics endpoints.
 *
 * @property value The API parameter value sent to Trakt
 */
public enum class TimePeriod(public val value: String) {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly"),
    ALL("all"),
}
