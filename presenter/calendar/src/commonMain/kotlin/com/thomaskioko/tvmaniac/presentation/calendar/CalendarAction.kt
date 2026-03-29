package com.thomaskioko.tvmaniac.presentation.calendar

public sealed interface CalendarAction

public data object RefreshCalendar : CalendarAction

public data object NavigateToPreviousWeek : CalendarAction

public data object NavigateToNextWeek : CalendarAction

public data class EpisodeCardClicked(val episodeTraktId: Long) : CalendarAction

public data class MessageShown(val id: Long) : CalendarAction
