package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

interface TraktUserRemoteDataSource {

    suspend fun getUser(userId: String): ApiResponse<TraktUserResponse, ErrorResponse>

    suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>
}
