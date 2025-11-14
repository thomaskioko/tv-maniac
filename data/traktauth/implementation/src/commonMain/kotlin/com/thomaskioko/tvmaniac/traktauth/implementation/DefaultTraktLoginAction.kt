package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.SimpleAuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktLoginAction
import kotlinx.coroutines.suspendCancellableCoroutine
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.coroutines.resume
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTraktLoginAction(
    private val logger: Logger,
) : TraktLoginAction {

    private var pendingCallback: ((AuthState?) -> Unit)? = null
    override var lastError: AuthError? = null
        private set

    override suspend fun invoke(): AuthState? {
        return suspendCancellableCoroutine { continuation ->
            pendingCallback = { result ->
                continuation.resume(result)
            }

            continuation.invokeOnCancellation {
                pendingCallback = null
            }
        }
    }

    override fun onTokensReceived(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long?,
    ) {
        logger.debug("TraktLoginAction: onTokensReceived $accessToken, $refreshToken, $expiresAtSeconds")
        lastError = null
        val expiresAt = expiresAtSeconds?.let {
            Instant.fromEpochSeconds(it)
        } ?: (Clock.System.now() + 86400.seconds)
        val authState = SimpleAuthState(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isAuthorized = true,
            expiresAt = expiresAt,
        )
        pendingCallback?.invoke(authState)
        pendingCallback = null
    }

    override fun onError(error: AuthError) {
        logger.error("TraktLoginAction", "OAuth error: $error")
        lastError = error
        pendingCallback?.invoke(null)
        pendingCallback = null
    }
}
