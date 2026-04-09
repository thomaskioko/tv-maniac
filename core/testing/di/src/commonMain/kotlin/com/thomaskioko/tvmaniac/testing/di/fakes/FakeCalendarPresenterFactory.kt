package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarAction
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, CalendarPresenter.Factory::class)
public class FakeCalendarPresenterFactory : CalendarPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showId: Long) -> Unit,
        onEpisodeLongPressed: (episodeId: Long) -> Unit,
    ): CalendarPresenter = FakeCalendarPresenter()
}

internal class FakeCalendarPresenter : CalendarPresenter {
    override val state: StateFlow<CalendarState> = MutableStateFlow(CalendarState())
    override val stateValue: Value<CalendarState> = MutableValue(CalendarState())

    override fun dispatch(action: CalendarAction) {
    }
}
