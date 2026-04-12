package com.thomaskioko.tvmaniac.presentation.progress

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@AssistedInject
public class ProgressPresenter(
    @Assisted componentContext: ComponentContext,
    upNextPresenterFactory: UpNextPresenter.Factory,
    calendarPresenterFactory: CalendarPresenter.Factory,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(ProgressState())

    public val state: StateFlow<ProgressState> = _state.asStateFlow()

    public val stateValue: Value<ProgressState> = state.asValue(coroutineScope)

    public val upNextPresenter: UpNextPresenter = upNextPresenterFactory.create(
        componentContext = childContext(key = "UpNext"),
    )

    public val calendarPresenter: CalendarPresenter = calendarPresenterFactory.create(
        componentContext = childContext(key = "Calendar"),
    )

    public fun dispatch(action: ProgressAction) {
        when (action) {
            is ProgressAction.SelectPage -> {
                _state.update { it.copy(selectedPage = action.index) }
            }
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(componentContext: ComponentContext): ProgressPresenter
    }
}
