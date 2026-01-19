package com.thomaskioko.tvmaniac.traktauth.api

public sealed interface TokenRefreshResult {
    public data object NotLoggedIn : TokenRefreshResult
    public data object TokenRevoked : TokenRefreshResult
    public data class Success(val authState: AuthState) : TokenRefreshResult
    public data class NetworkError(val message: String?) : TokenRefreshResult
    public data class Failed(val message: String?) : TokenRefreshResult
}
