package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUpNextNitroResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse

public interface TraktSyncRemoteDataSource {

    public suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse>

    public suspend fun getPlaybackEpisodes(
        limit: Int = 100,
    ): ApiResponse<List<TraktPlaybackEpisodeResponse>>

    public suspend fun getShowWatchedProgress(
        traktId: Long,
        lastActivity: String? = null,
        hidden: Boolean = false,
        specials: Boolean = false,
    ): ApiResponse<TraktWatchedProgressResponse>

    public suspend fun getUpNextNitro(
        intent: String = "continue",
        limit: Int = 100,
    ): ApiResponse<List<TraktUpNextNitroResponse>>
}
