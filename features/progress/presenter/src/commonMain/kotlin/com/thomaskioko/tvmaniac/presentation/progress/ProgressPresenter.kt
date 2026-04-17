package com.thomaskioko.tvmaniac.presentation.progress

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.TabScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Inject
@TabScreen(config = HomeConfig.Progress::class, parentScope = HomeRoute::class)
public class ProgressPresenter(
    componentContext: ComponentContext,
    progressChildGraphFactory: ProgressChildGraph.Factory,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(ProgressState())

    public val state: StateFlow<ProgressState> = _state.asStateFlow()

    public val stateValue: Value<ProgressState> = state.asValue(coroutineScope)

    public val upNextPresenter: UpNextPresenter =
        progressChildGraphFactory.createGraph(childContext(key = "UpNext")).upNextPresenter

    public val calendarPresenter: CalendarPresenter =
        progressChildGraphFactory.createGraph(childContext(key = "Calendar")).calendarPresenter

    public fun dispatch(action: ProgressAction) {
        when (action) {
            is ProgressAction.SelectPage -> {
                _state.update { it.copy(selectedPage = action.index) }
            }
        }
    }
}
