package com.thomaskioko.tvmaniac.domain.calendar

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.calendar.CalendarRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class FetchCalendarInteractor(
    private val repository: CalendarRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<FetchCalendarInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            repository.fetchCalendar(
                startDate = params.startDate,
                days = params.days,
                forceRefresh = params.forceRefresh,
            )
        }
    }

    public data class Params(
        val startDate: String,
        val days: Int,
        val forceRefresh: Boolean = false,
    )
}
