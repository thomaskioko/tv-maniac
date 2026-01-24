package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse

public interface TraktUserRemoteDataSource {

    public suspend fun getUser(userId: String): ApiResponse<TraktUserResponse>

    public suspend fun getUserStats(userId: String): ApiResponse<TraktUserStatsResponse>

    public suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>
}
