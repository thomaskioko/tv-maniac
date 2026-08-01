package com.thomaskioko.tvmaniac.datastore.api

public enum class ListStyle(public val value: String, public val isPremium: Boolean) {
    GRID("Grid", isPremium = false),
    LIST("List", isPremium = false),
    COMPACT("Compact", isPremium = true),
    DETAILED("Detailed", isPremium = true),
    ;

    public val freeFallback: ListStyle
        get() = when (this) {
            COMPACT -> LIST
            DETAILED -> GRID
            GRID, LIST -> this
        }
}
