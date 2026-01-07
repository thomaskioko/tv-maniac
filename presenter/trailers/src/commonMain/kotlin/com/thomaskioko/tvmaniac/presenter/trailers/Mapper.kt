package com.thomaskioko.tvmaniac.presenter.trailers

import com.thomaskioko.tvmaniac.db.SelectByShowId
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.presenter.trailers.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun List<SelectByShowId>.toTrailerList(): ImmutableList<Trailer> {
    return map {
        Trailer(
            showId = it.show_id.id,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg",
        )
    }
        .toImmutableList()
}
