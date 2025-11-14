package com.thomaskioko.tvmaniac.traktauth.api

interface TraktLoginAction {
    suspend operator fun invoke(): AuthState?
}
