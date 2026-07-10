package com.thomaskioko.tvmaniac.data.library.model

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import kotlin.time.Instant

public data class RemoteFollowedShow(
    val tmdbId: Long?,
    val imdbId: String?,
    val providerShowId: String?,
    val provider: SyncProviderSource,
    val title: String,
    val year: Int?,
    val followedAt: Instant,
)
