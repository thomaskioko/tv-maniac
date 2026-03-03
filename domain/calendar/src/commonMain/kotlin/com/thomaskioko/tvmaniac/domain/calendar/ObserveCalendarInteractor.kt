package com.thomaskioko.tvmaniac.domain.calendar

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.CalendarRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveCalendarInteractor(
    private val repository: CalendarRepository,
) : SubjectInteractor<ObserveCalendarInteractor.Params, List<CalendarEntry>>() {

    override fun createObservable(params: Params): Flow<List<CalendarEntry>> {
        return repository.observeCalendarEntries(params.startDate, params.endDate)
    }

    public data class Params(
        val startDate: Long,
        val endDate: Long,
    )
}
