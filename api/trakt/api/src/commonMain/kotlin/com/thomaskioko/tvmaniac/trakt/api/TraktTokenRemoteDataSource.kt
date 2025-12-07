package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse

interface TraktTokenRemoteDataSource {

    suspend fun getAccessToken(authCode: String): ApiResponse<TraktAccessTokenResponse>

    suspend fun getAccessRefreshToken(refreshToken: String): ApiResponse<TraktAccessRefreshTokenResponse>

    suspend fun revokeAccessToken(authCode: String)
}
