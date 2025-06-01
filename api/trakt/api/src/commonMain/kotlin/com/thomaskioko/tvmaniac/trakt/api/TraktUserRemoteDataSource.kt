package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

interface TraktUserRemoteDataSource {

    suspend fun getUser(userId: String): ApiResponse<TraktUserResponse>

    suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>
}
