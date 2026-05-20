package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUpNextNitroResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse

public interface TraktSyncRemoteDataSource {

    public suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse>

    public suspend fun getWatchedShows(
        limit: String = "100",
        extended: String = "full",
    ): ApiResponse<List<TraktWatchedShowResponse>>

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
