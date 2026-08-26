package com.thomaskioko.root.model

public object DeepLinkParser {

    public const val SCHEME: String = "tvmaniac"

    private const val HOST_SHOW = "show"
    private const val HOST_EPISODE = "episode"
    private const val SHOW_SEGMENTS = 1
    private const val EPISODE_SEGMENTS = 3

    public fun parse(url: String?): DeepLinkDestination? {
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
}
