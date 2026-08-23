package com.thomaskioko.tvmaniac.deeplink.implementation

import com.thomaskioko.tvmaniac.deeplink.api.DeepLink
import com.thomaskioko.tvmaniac.deeplink.api.DeepLinkParser
import com.thomaskioko.tvmaniac.deeplink.implementation.DeepLinkGrammar.EPISODE_SEGMENTS
import com.thomaskioko.tvmaniac.deeplink.implementation.DeepLinkGrammar.HOST_EPISODE
import com.thomaskioko.tvmaniac.deeplink.implementation.DeepLinkGrammar.HOST_SHOW
import com.thomaskioko.tvmaniac.deeplink.implementation.DeepLinkGrammar.SCHEME
import com.thomaskioko.tvmaniac.deeplink.implementation.DeepLinkGrammar.SHOW_SEGMENTS
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
public class DefaultDeepLinkParser : DeepLinkParser {

    override fun parse(url: String?): DeepLink? {
        val remainder = url?.trim()?.removePrefixOrNull("$SCHEME://") ?: return null
        val segments = remainder.substringBefore('?')
            .substringBefore('#')
            .split('/')
            .filter { it.isNotBlank() }
        val host = segments.firstOrNull() ?: return null
        val path = segments.drop(1)

        return when (host) {
            HOST_SHOW -> parseShow(path)
            HOST_EPISODE -> parseEpisode(path)
            else -> null
        }
    }

    private fun parseShow(path: List<String>): DeepLink? {
        if (path.size != SHOW_SEGMENTS) return null
        val showId = path[0].toLongOrNull() ?: return null
        return DeepLink.ShowDetails(showId = showId)
    }

    private fun parseEpisode(path: List<String>): DeepLink? {
        if (path.size != EPISODE_SEGMENTS) return null
        val showId = path[0].toLongOrNull() ?: return null
        val seasonNumber = path[1].toLongOrNull() ?: return null
        val episodeNumber = path[2].toLongOrNull() ?: return null
        return DeepLink.Episode(
            showId = showId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
        )
    }

    private fun String.removePrefixOrNull(prefix: String): String? =
        if (startsWith(prefix, ignoreCase = true)) substring(prefix.length) else null
}
