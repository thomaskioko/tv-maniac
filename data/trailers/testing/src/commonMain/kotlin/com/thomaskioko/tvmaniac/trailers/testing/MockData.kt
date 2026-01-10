package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SelectByShowTmdbId

public val trailers: List<SelectByShowTmdbId> = listOf(
    SelectByShowTmdbId(
        trailer_id = "1231",
        show_tmdb_id = Id(84958),
        key = "Fd43V",
        name = "Some title",
        site = "Youtube",
        size = 1231,
        type = "type",
    ),
)
