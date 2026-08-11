package com.thomaskioko.tvmaniac.data.rewatch.api

/**
 * A single rewatch session as reported by the active provider.
 *
 * The two providers fill this differently. Simkl returns one row per show: a session-level
 * aggregate with [providerSessionId], [episodeCount], [status] and [startedAt] set, and
 * [seasonNumber] and [episodeNumber] null, since Simkl does not say which episodes made up the
 * count. Trakt returns one row per rewatched episode: [seasonNumber] and [episodeNumber] set, and
 * [providerSessionId] and [startedAt] null, since Trakt has no session concept.
 *
 * Only a session carrying a [providerSessionId] can be written to the local table, since that id is
 * what makes a repeated sync land on the row it wrote last time.
 */
public data class RemoteRewatchSession(
    val providerSessionId: Long?,
    val seasonNumber: Long?,
    val episodeNumber: Long?,
    val episodeCount: Long?,
    val lastWatchedAt: Long?,
    val status: RemoteRewatchSessionStatus = RemoteRewatchSessionStatus.ACTIVE,
    val startedAt: Long? = null,
)
