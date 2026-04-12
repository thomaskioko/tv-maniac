package com.thomaskioko.tvmaniac.presenter.trailers

import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
import com.thomaskioko.tvmaniac.presenter.trailers.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun List<SelectByShowTraktId>.toTrailerList(): ImmutableList<Trailer> {
    return map { trailer ->
        Trailer(
            showTmdbId = trailer.show_tmdb_id.id,
            key = trailer.trailer_id,
            name = trailer.name,
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/${trailer.trailer_id}/hqdefault.jpg",
        )
    }.toImmutableList()
}
