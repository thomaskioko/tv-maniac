package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState

/**
 * Provider-neutral auth repository: an [AccountAuthRepository] plus the suspend [getAuthState] accessor
 * an HTTP client needs to attach a bearer token. The generic OAuth holder implements this; provider
 * facades (e.g. Trakt) extend it.
 */
public interface OAuthRepository : AccountAuthRepository {
    public suspend fun getAuthState(): AuthState?
}
