package com.thomaskioko.tvmaniac.presenter.trailers

import com.thomaskioko.tvmaniac.presenter.trailers.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer as DomainTrailer

internal fun List<DomainTrailer>.toTrailerList(): ImmutableList<Trailer> {
    return map { trailer ->
        Trailer(
            showId = trailer.showId,
            key = trailer.key,
            name = trailer.name,
            youtubeThumbnailUrl = trailer.youtubeThumbnailUrl,
        )
    }.toImmutableList()
}
