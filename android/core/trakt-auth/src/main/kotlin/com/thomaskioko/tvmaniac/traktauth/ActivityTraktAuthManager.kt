package com.thomaskioko.tvmaniac.traktauth

import android.app.Activity
import android.content.Intent
import co.touchlab.kermit.Logger
import me.tatarka.inject.annotations.Inject
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientAuthentication

@Inject
class ActivityTraktAuthManager(
    activity: Activity,
    private val traktManagerRepository: TraktManagerRepository,
    private val requestProvider: Lazy<AuthorizationRequest>,
    private val clientAuth: Lazy<ClientAuthentication>,
) : TraktAuthManager {

    private val authService = AuthorizationService(activity)

    override fun buildLoginIntent(): Intent {
        return authService.getAuthorizationRequestIntent(requestProvider.value)
    }

    override fun onLoginResult(result: TraktActivityResultContract.Result) {
        val (response, error) = result
        when {
            response != null -> {
                authService.performTokenRequest(
                    response.createTokenExchangeRequest(),
                    clientAuth.value
                ) { tokenResponse, ex ->
                    val state = AuthState().apply {
                        update(tokenResponse, ex)
                    }
                    traktManagerRepository.onNewAuthState(state)
                }
            }
            error != null -> Logger.e("AuthException", error)
        }
    }
}
