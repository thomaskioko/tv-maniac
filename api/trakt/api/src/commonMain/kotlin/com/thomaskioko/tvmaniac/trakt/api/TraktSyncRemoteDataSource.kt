package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUpNextNitroResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse

public interface TraktSyncRemoteDataSource {

    public suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse>

    public suspend fun getPlaybackEpisodes(
        limit: Int = 100,
    ): ApiResponse<List<TraktPlaybackEpisodeResponse>>

    public suspend fun getShowWatchedProgress(
        showId: Long,
        lastActivity: String? = null,
        hidden: Boolean = false,
        specials: Boolean = false,
    ): ApiResponse<TraktWatchedProgressResponse>

    public suspend fun getUpNextNitro(
        intent: String = "all",
        limit: Int = 100,
        page: Int = 1,
    ): ApiResponse<List<TraktUpNextNitroResponse>>

    /**
     * Fetches the authenticated user's full watched-shows feed.
     *
     * With `extended = "noseasons"` Trakt returns one row per show; with `extended = "progress"`
     * each row carries the per-season per-episode `last_watched_at` breakdown the bulk
     * watched-episode sync needs to populate the local `watched_episodes` table.
     *
     * The endpoint is paginated by Trakt; callers must drain pages until the response is empty.
     *
     * @param page 1-indexed page number.
     * @param limit page size; Trakt's documented maximum is 100.
     * @param extended Trakt `extended` query parameter. Defaults to `"noseasons"` for
     *   backwards compatibility with the Continue Watching caller.
     */
    public suspend fun getWatchedShows(
        page: Int = 1,
        limit: Int = 100,
        extended: String = "noseasons",
    ): ApiResponse<List<TraktWatchedShowResponse>>
}
