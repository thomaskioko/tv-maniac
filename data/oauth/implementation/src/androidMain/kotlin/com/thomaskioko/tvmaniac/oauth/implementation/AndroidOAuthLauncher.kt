package com.thomaskioko.tvmaniac.oauth.implementation

import android.content.ActivityNotFoundException
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.oauth.api.AuthStateHolder
import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientSecretPost
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class AndroidOAuthLauncher(
    private val activity: ComponentActivity,
    private val authStateHolder: AuthStateHolder,
    private val authClientConfigs: Map<AccountProvider, AuthClientConfig>,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
) : OAuthLauncher {

    private val authService by lazy { AuthorizationService(activity) }
    private lateinit var launcher: ActivityResultLauncher<AuthorizationRequest>

    @Volatile
    private var pendingProvider: AccountProvider? = null

    override fun register() {
        if (::launcher.isInitialized) return
        launcher = activity.registerForActivityResult(OAuthActivityResultContract(authService)) { result ->
            if (result != null) {
                coroutineScope.launch { onResult(result) }
            }
        }
    }

    override fun launch(config: AuthClientConfig) {
        check(::launcher.isInitialized) {
            "register() must be called before launch(). Call it in the Activity's onCreate()/onStart()."
        }
        pendingProvider = config.provider
        try {
            launcher.launch(buildRequest(config))
        } catch (e: ActivityNotFoundException) {
            logger.error(LOG_TAG, e)
            coroutineScope.launch { authStateHolder.setAuthError(config.provider, AuthError.NoBrowserAvailable) }
        }
    }

    override fun setCallback(provider: AccountProvider, callback: () -> Unit) {
        // Not used on Android - results arrive through the ActivityResultLauncher.
    }

    private fun buildRequest(config: AuthClientConfig): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(
            config.authorizationEndpoint.toUri(),
            config.tokenEndpoint.toUri(),
        )
        return AuthorizationRequest.Builder(
            serviceConfig,
            config.clientId,
            ResponseTypeValues.CODE,
            config.redirectUri.toUri(),
        )
            .apply {
                if (config.scopes.isNotEmpty()) setScopes(config.scopes)
                setCodeVerifier(null)
            }
            .build()
    }

    private suspend fun onResult(result: OAuthActivityResultContract.Result) {
        val (response, error) = result
        when {
            response != null -> {
                val config = requireNotNull(
                    authClientConfigs.values.firstOrNull { it.clientId == response.request.clientId }
                        ?: pendingProvider?.let { authClientConfigs[it] },
                ) { "No AuthClientConfig matched the OAuth response (clientId=${response.request.clientId})." }
                exchangeAuthorizationCode(response.createTokenExchangeRequest(), config)
            }
            error != null -> {
                logger.error(LOG_TAG, error)
                val provider = pendingProvider
                if (provider == null) {
                    logger.warning(LOG_TAG, "OAuth error after process recreation; no pending provider to route it to.")
                    return
                }
                val authError = when (error.type) {
                    TYPE_USER_CANCELED -> AuthError.OAuthCancelled
                    else -> AuthError.OAuthFailed(error.error ?: "Unknown OAuth error")
                }
                authStateHolder.setAuthError(provider, authError)
            }
        }
    }

    private suspend fun exchangeAuthorizationCode(tokenRequest: TokenRequest, config: AuthClientConfig) {
        try {
            val (tokenResponse, tokenException) = suspendCoroutine { continuation ->
                authService.performTokenRequest(
                    tokenRequest,
                    ClientSecretPost(config.clientSecret),
                ) { response, exception ->
                    continuation.resume(response to exception)
                }
            }

            if (tokenException != null) {
                logger.error(LOG_TAG, tokenException)
                authStateHolder.setAuthError(config.provider, AuthError.TokenExchangeFailed)
                return
            }

            val accessToken = tokenResponse?.accessToken
            if (accessToken.isNullOrBlank()) {
                logger.error(LOG_TAG, Throwable("Token response missing access token"))
                authStateHolder.setAuthError(config.provider, AuthError.TokenExchangeFailed)
                return
            }

            val expiresAtSeconds = tokenResponse.accessTokenExpirationTime?.let { it / 1000 } ?: NEVER_EXPIRES_SECONDS
            authStateHolder.saveTokens(
                provider = config.provider,
                accessToken = accessToken,
                refreshToken = tokenResponse.refreshToken.orEmpty(),
                expiresAtSeconds = expiresAtSeconds,
            )
        } catch (e: Exception) {
            logger.error(LOG_TAG, e)
            authStateHolder.setAuthError(config.provider, AuthError.TokenExchangeFailed)
        }
    }

    private companion object {
        private const val LOG_TAG = "OAuthLauncher"
        private const val TYPE_USER_CANCELED = 2

        // Providers whose tokens never expire (e.g. Simkl) report no expiry; treat it as far-future so
        // the refresh path is never taken. Revocation is handled by the 401 -> logout path instead.
        private const val NEVER_EXPIRES_SECONDS = 4_102_444_800L
    }
}
