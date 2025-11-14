package com.thomaskioko.tvmaniac.traktauth.api

sealed class AuthError {
    data class OAuthFailed(val message: String) : AuthError()
    data object NetworkError : AuthError()
    data object OAuthCancelled : AuthError()
    data object TokenExchangeFailed : AuthError()
    data object Unknown : AuthError()
}
