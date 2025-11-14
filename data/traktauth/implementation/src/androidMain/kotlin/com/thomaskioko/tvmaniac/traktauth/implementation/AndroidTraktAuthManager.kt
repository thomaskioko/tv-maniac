package com.thomaskioko.tvmaniac.traktauth.implementation

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktLoginAction
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.TokenRequest
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
class AndroidTraktAuthManager(
    private val activity: ComponentActivity,
    private val traktActivityResultContract: TraktActivityResultContract,
    private val loginAction: TraktLoginAction,
    private val clientAuth: Lazy<ClientAuthentication>,
    private val authService: Lazy<AuthorizationService>,
    private val logger: Logger,
) : TraktAuthManager {

    private lateinit var launcher: ActivityResultLauncher<Unit>

    override fun registerResult() {
        launcher = activity.registerForActivityResult(traktActivityResultContract) { result ->
            if (result != null) {
                onLoginResult(result)
            }
        }
    }

    override fun launchWebView() = launcher.launch(Unit)

    private fun onLoginResult(result: TraktActivityResultContract.Result) {
        val (response, error) = result
        when {
            response != null -> {
                activity.lifecycleScope.launch {
                    exchangeAuthorizationCode(response.createTokenExchangeRequest())
                }
            }

            error != null -> {
                logger.error("AuthException", error)
                val authError = when (error.type) {
                    2 -> AuthError.OAuthCancelled
                    else -> AuthError.OAuthFailed(error.error ?: "Unknown OAuth error")
                }
                loginAction.onError(authError)
            }
        }
    }

    private suspend fun exchangeAuthorizationCode(tokenRequest: TokenRequest) {
        try {
            val tokenResponse = suspendCoroutine { continuation ->
                authService.value.performTokenRequest(
                    tokenRequest,
                    clientAuth.value,
                ) { response, exception ->
                    if (exception != null) {
                        continuation.resume(null)
                    } else {
                        continuation.resume(response)
                    }
                }
            }

            if (tokenResponse != null) {
                val expiresAtSeconds = tokenResponse.accessTokenExpirationTime?.let {
                    it / 1000
                }

                loginAction.onTokensReceived(
                    accessToken = tokenResponse.accessToken.orEmpty(),
                    refreshToken = tokenResponse.refreshToken.orEmpty(),
                    expiresAtSeconds = expiresAtSeconds,
                )

                logger.debug("Token exchange successful")
            } else {
                logger.error("TokenExchangeError", Throwable("Token exchange returned null response"))
                loginAction.onError(AuthError.TokenExchangeFailed)
            }
        } catch (e: Exception) {
            logger.error("TokenExchangeException", e)
            loginAction.onError(AuthError.TokenExchangeFailed)
        }
    }
}
