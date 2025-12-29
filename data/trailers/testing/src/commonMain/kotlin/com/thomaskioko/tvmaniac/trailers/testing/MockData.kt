package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Trailers

public val trailers: List<Trailers> = listOf(
    Trailers(
        id = "1231",
        show_id = Id(84958),
        key = "Fd43V",
        name = "Some title",
        site = "Youtube",
        size = 1231,
        type = "type",
    ),
)
