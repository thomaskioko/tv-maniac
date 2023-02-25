package com.thomaskioko.tvmaniac.domain.trailers.api

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.domain.trailers.api.model.Trailer

internal fun List<Trailers>?.toTrailerList(): List<Trailer> {
    return this?.map {
        Trailer(
            showId = it.trakt_id,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg"
        )
    } ?: emptyList()
}