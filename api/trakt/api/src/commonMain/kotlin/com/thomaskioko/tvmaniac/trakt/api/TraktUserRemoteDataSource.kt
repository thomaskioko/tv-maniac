package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse

interface TraktUserRemoteDataSource {

    suspend fun getUser(userId: String): ApiResponse<TraktUserResponse>

    suspend fun getUserStats(userId: String): ApiResponse<TraktUserStatsResponse>

    suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>
}
