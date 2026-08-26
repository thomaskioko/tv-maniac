package com.thomaskioko.root.model

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
public class DefaultDeepLinkParser : DeepLinkParser {

    override fun parse(url: String?): DeepLinkDestination? {
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

    private fun parseShow(path: List<String>): DeepLinkDestination? {
        if (path.size != SHOW_SEGMENTS) return null
        val showId = path[0].toLongOrNull() ?: return null
        return DeepLinkDestination.ShowDetails(showId = showId)
    }

    private fun parseEpisode(path: List<String>): DeepLinkDestination? {
        if (path.size != EPISODE_SEGMENTS) return null
        val showId = path[0].toLongOrNull() ?: return null
        val seasonNumber = path[1].toLongOrNull() ?: return null
        val episodeNumber = path[2].toLongOrNull() ?: return null
        return DeepLinkDestination.Episode(
            showId = showId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
        )
    }

    private fun String.removePrefixOrNull(prefix: String): String? =
        if (startsWith(prefix, ignoreCase = true)) substring(prefix.length) else null

    private companion object {
        const val SCHEME = "tvmaniac"
        const val HOST_SHOW = "show"
        const val HOST_EPISODE = "episode"
        const val SHOW_SEGMENTS = 1
        const val EPISODE_SEGMENTS = 3
    }
}
