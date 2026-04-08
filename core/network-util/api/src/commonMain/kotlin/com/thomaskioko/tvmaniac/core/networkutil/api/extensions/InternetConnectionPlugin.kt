package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.networkutil.api.model.NoInternetException
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

public class InternetConnectionPluginConfig {
    public var internetConnectionChecker: InternetConnectionChecker? = null
}

public val InternetConnectionPlugin: ClientPlugin<InternetConnectionPluginConfig> = createClientPlugin(
    "InternetConnectionPlugin",
    ::InternetConnectionPluginConfig,
) {
    val checker = pluginConfig.internetConnectionChecker ?: return@createClientPlugin

    onRequest { _, _ ->
        if (!checker.isConnected()) {
            throw NoInternetException
        }
    }
}
