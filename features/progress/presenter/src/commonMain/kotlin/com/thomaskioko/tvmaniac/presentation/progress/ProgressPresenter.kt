package com.thomaskioko.tvmaniac.presentation.progress

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.calendar.di.CalendarChildGraph
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.di.UpNextChildGraph
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@Inject
@NavDestination(
    route = ProgressRoot::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.TAB_ROOT,
)
public class ProgressPresenter(
    componentContext: ComponentContext,
    upNextGraphFactory: UpNextChildGraph.Factory,
    calendarGraphFactory: CalendarChildGraph.Factory,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(ProgressState())

    public val upNextPresenter: UpNextPresenter =
        upNextGraphFactory.createUpNextGraph(childContext(key = "UpNext")).upNextPresenter

    public val calendarPresenter: CalendarPresenter =
        calendarGraphFactory.createCalendarGraph(childContext(key = "Calendar")).calendarPresenter

    public val state: StateFlow<ProgressState> = combine(
        _state,
        upNextPresenter.state,
        calendarPresenter.state,
    ) { progressState, upNextState, calendarState ->
        progressState.copy(
            isLoading = upNextState.isLoading || upNextState.isSyncing || calendarState.isLoading,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ProgressState(),
    )

    public val stateValue: Value<ProgressState> = state.asValue(coroutineScope)

    public fun dispatch(action: ProgressAction) {
        when (action) {
            is ProgressAction.SelectPage -> {
                _state.update { it.copy(selectedPage = action.index) }
            }
        }
    }
}
