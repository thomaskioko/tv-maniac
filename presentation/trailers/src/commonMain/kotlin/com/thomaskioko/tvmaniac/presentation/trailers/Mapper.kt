package com.thomaskioko.tvmaniac.presentation.trailers

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer

internal fun List<Trailers>.toTrailerList(): List<Trailer> {
    return map {
        Trailer(
            showId = it.trakt_id,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg",
        )
    }
}
