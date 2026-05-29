package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFavoriteShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHiddenItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse

public interface TraktUserRemoteDataSource {

    public suspend fun getUser(userId: String): ApiResponse<TraktUserResponse>

    public suspend fun getUserStats(userId: String): ApiResponse<TraktUserStatsResponse>

    public suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>

    public suspend fun getFavoriteShows(userId: String): ApiResponse<List<TraktFavoriteShowResponse>>

    public suspend fun getHiddenProgressWatched(
        type: String = "show",
    ): ApiResponse<List<TraktHiddenItemResponse>>
}
