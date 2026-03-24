package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.RequiresAuth
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import io.ktor.client.plugins.api.createClientPlugin

/**
 * Configuration for [TraktAuthGuard].
 *
 * @property isAuthenticated Lambda that returns `true` when the current user has a valid
 *   session. Defaults to `{ false }` (unauthenticated).
 */
internal class TraktAuthConfig {
    var isAuthenticated: () -> Boolean = { false }
}

/**
 * Ktor client plugin that acts as a defense-in-depth guard for authenticated endpoints.
 *
 * On every outgoing request, the plugin checks whether the [RequiresAuth] attribute is set
 * to `true`. If it is and the user is not authenticated (per [TraktAuthConfig.isAuthenticated]),
 * the request is blocked immediately by throwing [AuthenticationException].
 *
 * In normal operation, [authSafeRequest][com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest]
 * performs its own pre-check and returns [ApiResponse.Unauthenticated] before the request
 * reaches this plugin. The guard therefore catches only the narrow race where the user logs
 * out between the `authSafeRequest` pre-check and the actual request execution, or cases
 * where a caller sets [RequiresAuth] without going through `authSafeRequest`.
 */
internal val TraktAuthGuard = createClientPlugin("TraktAuthGuard", ::TraktAuthConfig) {
    val isAuthenticated = pluginConfig.isAuthenticated

    onRequest { request, _ ->
        val requiresAuth = request.attributes.getOrNull(RequiresAuth) == true
        if (requiresAuth && !isAuthenticated()) {
            throw AuthenticationException(
                message = "Authentication required for ${request.method.value} ${request.url.buildString()}",
            )
        }
    }
}
