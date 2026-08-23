package com.thomaskioko.tvmaniac.deeplink.implementation

import com.thomaskioko.tvmaniac.deeplink.api.DeepLink
import com.thomaskioko.tvmaniac.deeplink.api.DeepLinkUrls
import com.thomaskioko.tvmaniac.deeplink.implementation.DeepLinkGrammar.HOST_EPISODE
import com.thomaskioko.tvmaniac.deeplink.implementation.DeepLinkGrammar.HOST_SHOW
import com.thomaskioko.tvmaniac.deeplink.implementation.DeepLinkGrammar.SCHEME
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
public class DefaultDeepLinkUrls : DeepLinkUrls {

    override fun urlFor(deepLink: DeepLink): String? = when (deepLink) {
        is DeepLink.ShowDetails -> "$SCHEME://$HOST_SHOW/${deepLink.showId}"
        is DeepLink.Episode ->
            "$SCHEME://$HOST_EPISODE/${deepLink.showId}/${deepLink.seasonNumber}/${deepLink.episodeNumber}"
        is DeepLink.SeasonDetails -> null
        DeepLink.DebugMenu -> null
    }
}
