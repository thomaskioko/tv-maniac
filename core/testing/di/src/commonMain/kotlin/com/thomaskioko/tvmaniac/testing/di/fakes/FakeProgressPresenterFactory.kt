package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarAction
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarState
import com.thomaskioko.tvmaniac.presentation.progress.ProgressAction
import com.thomaskioko.tvmaniac.presentation.progress.ProgressPresenter
import com.thomaskioko.tvmaniac.presentation.progress.ProgressState
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextAction
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, ProgressPresenter.Factory::class)
public class FakeProgressPresenterFactory : ProgressPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showId: Long) -> Unit,
        navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): ProgressPresenter = FakeProgressPresenter()
}

internal class FakeProgressPresenter : ProgressPresenter {
    override val state: StateFlow<ProgressState> = MutableStateFlow(ProgressState())

    override val upNextPresenter: UpNextPresenter = object : UpNextPresenter {
        override val state: StateFlow<UpNextState> = MutableStateFlow(UpNextState())
        override fun dispatch(action: UpNextAction) {}
    }

    override val calendarPresenter: CalendarPresenter = object : CalendarPresenter {
        override val state: StateFlow<CalendarState> = MutableStateFlow(CalendarState())
        override fun dispatch(action: CalendarAction) {}
    }

    override fun dispatch(action: ProgressAction) {}
}
