package com.thomaskioko.tvmaniac.traktauth.api

public sealed class AuthError {
    public data class OAuthFailed(val message: String) : AuthError()
    public data object NetworkError : AuthError()
    public data object OAuthCancelled : AuthError()
    public data object TokenExchangeFailed : AuthError()
    public data object Unknown : AuthError()
}
