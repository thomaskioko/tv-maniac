package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse

public interface TraktTokenRemoteDataSource {

    public suspend fun getAccessToken(authCode: String): ApiResponse<TraktAccessTokenResponse>

    public suspend fun getAccessRefreshToken(refreshToken: String): ApiResponse<TraktAccessRefreshTokenResponse>

    public suspend fun revokeAccessToken(authCode: String)
}
