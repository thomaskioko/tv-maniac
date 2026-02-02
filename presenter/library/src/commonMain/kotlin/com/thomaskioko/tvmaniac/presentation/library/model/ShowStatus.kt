package com.thomaskioko.tvmaniac.presentation.library.model

//TODO:: Load this from repository.
public enum class ShowStatus(public val displayName: String) {
    RETURNING_SERIES("Returning Series"),
    PLANNED("Planned"),
    IN_PRODUCTION("In Production"),
    ENDED("Ended"),
    CANCELED("Canceled"),
    ;

    public companion object {
        public fun fromDisplayName(displayName: String?): ShowStatus? {
            return entries.find { it.displayName.equals(displayName, ignoreCase = true) }
        }
    }
}
