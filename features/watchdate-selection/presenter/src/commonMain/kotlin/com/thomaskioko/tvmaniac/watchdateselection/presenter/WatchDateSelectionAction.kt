package com.thomaskioko.tvmaniac.watchdateselection.presenter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

public sealed interface WatchDateSelectionAction {
    public data object JustNowSelected : WatchDateSelectionAction
    public data object ReleaseDateSelected : WatchDateSelectionAction
    public data class OtherDateSelected(val date: LocalDate, val time: LocalTime) : WatchDateSelectionAction
    public data object UnknownDateSelected : WatchDateSelectionAction
    public data object Dismissed : WatchDateSelectionAction
}
