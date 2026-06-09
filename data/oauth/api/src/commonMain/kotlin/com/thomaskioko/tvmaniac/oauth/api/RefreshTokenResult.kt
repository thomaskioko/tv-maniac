package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState

public sealed class RefreshTokenResult {
    public data class Success(val authState: AuthState) : RefreshTokenResult()
    public data object TokenExpired : RefreshTokenResult()
    public data class NetworkError(val message: String?) : RefreshTokenResult()
    public data class Failed(val message: String?) : RefreshTokenResult()
}
