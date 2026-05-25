package com.thomaskioko.tvmaniac.myshows.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingPresenter
import com.thomaskioko.tvmaniac.continuewatching.presenter.di.ContinueWatchingChildGraph
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.myshows.nav.MyShowsRoot
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Inject
@NavDestination(
    route = MyShowsRoot::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.TAB_ROOT,
)
public class MyShowsPresenter(
    componentContext: ComponentContext,
    continueWatchingGraphFactory: ContinueWatchingChildGraph.Factory,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(MyShowsState())

    public val state: StateFlow<MyShowsState> = _state.asStateFlow()

    public val stateValue: Value<MyShowsState> = state.asValue(coroutineScope)

    public val continueWatchingPresenter: ContinueWatchingPresenter =
        continueWatchingGraphFactory
            .createContinueWatchingGraph(childContext(key = "ContinueWatching"))
            .continueWatchingPresenter

    public fun dispatch(action: MyShowsAction) {
        when (action) {
            is MyShowsAction.SelectPage -> {
                _state.update { it.copy(selectedPage = action.index) }
            }
        }
    }
}
