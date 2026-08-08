package com.thomaskioko.tvmaniac.shows.api

public fun traktGenreName(slug: String): String =
    slug.split("-").joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }

public fun traktGenreNames(tmdbGenre: String): List<String> = when (tmdbGenre) {
    "Action & Adventure" -> listOf("Action", "Adventure")
    "Sci-Fi & Fantasy" -> listOf("Science Fiction", "Fantasy")
    "War & Politics" -> listOf("War")
    "Kids" -> listOf("Children")
    "Talk" -> listOf("Talk Show")
    else -> listOf(tmdbGenre)
}
