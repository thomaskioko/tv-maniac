package com.thomaskioko.tvmaniac.presenter.trailers

import com.thomaskioko.tvmaniac.db.SelectByShowTmdbId
import com.thomaskioko.tvmaniac.presenter.trailers.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun List<SelectByShowTmdbId>.toTrailerList(): ImmutableList<Trailer> {
    return map {
        Trailer(
            showTmdbId = it.show_tmdb_id.id,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg",
        )
    }
        .toImmutableList()
}
