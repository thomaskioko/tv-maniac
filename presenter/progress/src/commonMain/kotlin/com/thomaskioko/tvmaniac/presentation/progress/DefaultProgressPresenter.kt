package com.thomaskioko.tvmaniac.presentation.progress

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, ProgressPresenter::class)
public class DefaultProgressPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToShowDetails: (showId: Long) -> Unit,
    @Assisted private val navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    private val upNextPresenterFactory: UpNextPresenter.Factory,
    private val calendarPresenterFactory: CalendarPresenter.Factory,
) : ProgressPresenter, ComponentContext by componentContext {
    private val _state = MutableStateFlow(ProgressState())

    override val state: StateFlow<ProgressState> = _state.asStateFlow()

    override val upNextPresenter: UpNextPresenter = upNextPresenterFactory(
        componentContext = childContext(key = "UpNext"),
        navigateToShowDetails = navigateToShowDetails,
        navigateToSeasonDetails = navigateToSeasonDetails,
    )

    override val calendarPresenter: CalendarPresenter = calendarPresenterFactory(
        componentContext = childContext(key = "Calendar"),
        navigateToShowDetails = navigateToShowDetails,
    )

    override fun dispatch(action: ProgressAction) {
        when (action) {
            is ProgressAction.SelectPage -> {
                _state.update { it.copy(selectedPage = action.index) }
            }
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, ProgressPresenter.Factory::class)
public class DefaultProgressPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        navigateToShowDetails: (showId: Long) -> Unit,
        navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ) -> ProgressPresenter,
) : ProgressPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showId: Long) -> Unit,
        navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): ProgressPresenter = presenter(componentContext, navigateToShowDetails, navigateToSeasonDetails)
}
