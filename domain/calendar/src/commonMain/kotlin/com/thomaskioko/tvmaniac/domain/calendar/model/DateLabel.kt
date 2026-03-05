package com.thomaskioko.tvmaniac.domain.calendar.model

public sealed interface DateLabel {
    public val formattedDate: String

    public data class Today(override val formattedDate: String) : DateLabel
    public data class Tomorrow(override val formattedDate: String) : DateLabel
    public data class DayOfWeek(val dayName: String, override val formattedDate: String) : DateLabel
}
