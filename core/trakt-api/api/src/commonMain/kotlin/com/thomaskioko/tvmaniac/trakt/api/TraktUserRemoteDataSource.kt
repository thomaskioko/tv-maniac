package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.util.model.ApiResponse

interface TraktUserRemoteDataSource {

  suspend fun getUser(userId: String): ApiResponse<TraktUserResponse>

  suspend fun getUserList(userId: String): List<TraktPersonalListsResponse>
}
