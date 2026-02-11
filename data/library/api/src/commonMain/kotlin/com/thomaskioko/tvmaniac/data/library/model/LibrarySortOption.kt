package com.thomaskioko.tvmaniac.data.library.model

public enum class LibrarySortOption(public val sortBy: String, public val sortHow: String) {
    RANK_ASC("rank", "asc"),
    RANK_DESC("rank", "desc"),
    ADDED_DESC("added", "desc"),
    ADDED_ASC("added", "asc"),
    RELEASED_DESC("released", "desc"),
    RELEASED_ASC("released", "asc"),
    TITLE_ASC("title", "asc"),
    TITLE_DESC("title", "desc"),
}
