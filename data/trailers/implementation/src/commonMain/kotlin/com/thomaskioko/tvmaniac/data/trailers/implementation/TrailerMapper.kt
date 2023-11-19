package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailerResponse

fun List<TrailerResponse>.toEntity(id: Long) = map { trailer ->
    Trailers(
        id = trailer.id,
        show_id = Id(id),
        key = trailer.key,
        name = trailer.name,
        site = trailer.site,
        size = trailer.size.toLong(),
        type = trailer.type,
    )
}
