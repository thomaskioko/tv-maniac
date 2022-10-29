package com.thomaskioko.tvmaniac.traktauth

import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientAuthentication
import dagger.Lazy
import javax.inject.Inject

internal class ActivityTraktAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val traktManager: TraktManager,
    private val requestProvider: Lazy<AuthorizationRequest>,
    private val clientAuth: Lazy<ClientAuthentication>,
) : TraktAuthManager {
    private val authService = AuthorizationService(context)

    override fun buildLoginIntent(): Intent {
        return authService.getAuthorizationRequestIntent(requestProvider.get())
    }

    override fun onLoginResult(result: LoginTrakt.Result) {
        val (response, error) = result
        when {
            response != null -> {
                authService.performTokenRequest(
                    response.createTokenExchangeRequest(),
                    clientAuth.get()
                ) { tokenResponse, ex ->
                    val state = AuthState().apply {
                        update(tokenResponse, ex)
                    }
                    traktManager.onNewAuthState(state)
                }
            }
            error != null -> Logger.e("AuthException", error)
        }
    }
}
