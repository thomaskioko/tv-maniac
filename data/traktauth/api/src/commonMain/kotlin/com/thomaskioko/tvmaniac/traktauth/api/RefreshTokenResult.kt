package com.thomaskioko.tvmaniac.traktauth.api

public sealed class RefreshTokenResult {
    public data class Success(val authState: AuthState) : RefreshTokenResult()
    public data object TokenExpired : RefreshTokenResult()
    public data object Failed : RefreshTokenResult()
}
