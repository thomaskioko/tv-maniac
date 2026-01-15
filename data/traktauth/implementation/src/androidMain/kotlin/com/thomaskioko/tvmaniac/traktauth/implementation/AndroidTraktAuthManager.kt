package com.thomaskioko.tvmaniac.traktauth.implementation

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.TokenRequest
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TYPE_USER_CANCELED = 2

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class AndroidTraktAuthManager(
    private val activity: ComponentActivity,
    private val traktActivityResultContract: TraktActivityResultContract,
    private val loginAction: TraktAuthRepository,
    private val clientAuth: Lazy<ClientAuthentication>,
    private val authService: Lazy<AuthorizationService>,
    private val logger: Logger,
    private val coroutineScope: AppCoroutineScope,
) : TraktAuthManager {

    private lateinit var launcher: ActivityResultLauncher<Unit>

    override fun registerResult() {
        launcher = activity.registerForActivityResult(traktActivityResultContract) { result ->
            if (result != null) {
                coroutineScope.io.launch {
                    onLoginResult(result)
                }
            }
        }
    }

    override fun launchWebView() {
        check(::launcher.isInitialized) {
            "registerResult() must be called before launchWebView(). " +
                "Call registerResult() in your Activity's onCreate() or onStart()."
        }
        launcher.launch(Unit)
    }

    override fun setAuthCallback(callback: () -> Unit) {
        // Not used on Android - auth handled via ActivityResultLauncher
    }

    private suspend fun onLoginResult(result: TraktActivityResultContract.Result) {
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
                    TYPE_USER_CANCELED -> AuthError.OAuthCancelled
                    else -> AuthError.OAuthFailed(error.error ?: "Unknown OAuth error")
                }
                loginAction.setAuthError(authError)
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
                logger.debug("@exchangeAuthorizationCode Token Response: $tokenResponse" )

                val accessToken = tokenResponse.accessToken
                val refreshToken = tokenResponse.refreshToken
                val expiresAtMillis = tokenResponse.accessTokenExpirationTime

                if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                    logger.error("TokenExchangeError", Throwable("Token response missing access or refresh token"))
                    loginAction.setAuthError(AuthError.TokenExchangeFailed)
                    return
                }

                if (expiresAtMillis == null) {
                    logger.error("TokenExchangeError", Throwable("Token response missing expiration time"))
                    loginAction.setAuthError(AuthError.TokenExchangeFailed)
                    return
                }

                loginAction.saveTokens(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresAtSeconds = expiresAtMillis / 1000,
                )

                logger.debug("TraktAuthManager", "Token exchange successful")
            } else {
                logger.error("TokenExchangeError", Throwable("Token exchange returned null response"))
                loginAction.setAuthError(AuthError.TokenExchangeFailed)
            }
        } catch (e: Exception) {
            logger.error("TokenExchangeException", e)
            loginAction.setAuthError(AuthError.TokenExchangeFailed)
        }
    }
}
