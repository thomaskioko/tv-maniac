package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.RequiresAuth
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import io.ktor.client.plugins.api.createClientPlugin

internal class SimklAuthConfig {
    var isAuthenticated: () -> Boolean = { false }
}

internal val SimklAuthGuard = createClientPlugin("SimklAuthGuard", ::SimklAuthConfig) {
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
