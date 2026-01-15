package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SelectByShowTraktId

public val trailers: List<SelectByShowTraktId> = listOf(
    SelectByShowTraktId(
        trailer_id = "Fd43V",
        show_tmdb_id = Id(84958),
        show_trakt_id = Id(84958),
        youtube_url = "https://www.youtube.com/watch?v=Fd43V",
        name = "Some title",
        site = "Youtube",
        size = 1231,
        type = "type",
    ),
)
