package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory

internal const val GENRE_PAGE_SIZE: Int = 10

internal fun genreShowsRequestId(slug: String, category: GenreShowCategory, page: Long): Long =
    "${slug}_${category.name}_$page".hashCode().toLong()
