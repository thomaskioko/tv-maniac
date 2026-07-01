package com.thomaskioko.tvmaniac.myshows.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingPresenter
import com.thomaskioko.tvmaniac.continuewatching.presenter.di.ContinueWatchingChildGraph
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.myshows.nav.MyShowsRoot
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingPresenter
import com.thomaskioko.tvmaniac.startwatching.presenter.di.StartWatchingChildGraph
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
@NavDestination(
    route = MyShowsRoot::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.TAB_ROOT,
)
public class MyShowsPresenter(
    componentContext: ComponentContext,
    localizer: Localizer,
    private val repository: WatchlistPrefsRepository,
    continueWatchingGraphFactory: ContinueWatchingChildGraph.Factory,
    startWatchingGraphFactory: StartWatchingChildGraph.Factory,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val continueWatchingTitle = localizer.getString(StringResourceKey.LabelContinueWatching)
    private val startWatchingTitle = localizer.getString(StringResourceKey.LabelStartWatching)

    private val selectedPage = MutableStateFlow(0)
    private val query = MutableStateFlow("")
    private val searchActive = MutableStateFlow(false)

    public val continueWatchingPresenter: ContinueWatchingPresenter =
        continueWatchingGraphFactory
            .createContinueWatchingGraph(childContext(key = "ContinueWatching"))
            .continueWatchingPresenter

    public val startWatchingPresenter: StartWatchingPresenter =
        startWatchingGraphFactory
            .createStartWatchingGraph(childContext(key = "StartWatching"))
            .startWatchingPresenter

    public val state: StateFlow<MyShowsState> = combine(
        selectedPage,
        query,
        searchActive,
        repository.observeListStyle(),
        repository.observeSortOption(),
        continueWatchingPresenter.state,
        startWatchingPresenter.state,
    ) { page, currentQuery, isSearchActive, isGridMode, sortOption, continueWatching, startWatching ->
        MyShowsState(
            selectedPage = page,
            continueWatchingTitle = continueWatchingTitle,
            startWatchingTitle = startWatchingTitle,
            query = currentQuery,
            isSearchActive = isSearchActive,
            isGridMode = isGridMode,
            sortOption = sortOption,
            showRefreshIndicator = if (page == 0) {
                continueWatching.showRefreshIndicator
            } else {
                startWatching.showRefreshIndicator
            },
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MyShowsState(
            continueWatchingTitle = continueWatchingTitle,
            startWatchingTitle = startWatchingTitle,
        ),
    )

    public val stateValue: Value<MyShowsState> = state.asValue(coroutineScope)

    public fun dispatch(action: MyShowsAction) {
        when (action) {
            is MyShowsAction.SelectPage -> selectedPage.value = action.index
            is MyShowsAction.QueryChanged -> updateQuery(action.query)
            is MyShowsAction.ClearQuery -> updateQuery("")
            is MyShowsAction.ToggleSearch -> toggleSearch()
            is MyShowsAction.ChangeListStyle -> coroutineScope.launch {
                repository.saveListStyle(!action.isGridMode)
            }
            is MyShowsAction.ChangeSortOption -> coroutineScope.launch {
                repository.saveSortOption(action.sortOption)
            }
        }
    }

    private fun updateQuery(value: String) {
        query.value = value
        continueWatchingPresenter.onQueryChanged(value)
        startWatchingPresenter.onQueryChanged(value)
    }

    private fun toggleSearch() {
        val nowActive = !searchActive.value
        searchActive.value = nowActive
        if (!nowActive) {
            updateQuery("")
        }
    }
}
