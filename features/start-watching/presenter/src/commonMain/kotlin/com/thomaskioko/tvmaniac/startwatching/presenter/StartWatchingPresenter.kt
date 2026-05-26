package com.thomaskioko.tvmaniac.startwatching.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.myshows.nav.MyShowsRoot
import com.thomaskioko.tvmaniac.myshows.nav.scope.MyShowsChildScope
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@ChildPresenter(scope = MyShowsChildScope::class, parentScope = MyShowsRoot::class)
@Inject
public class StartWatchingPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    observeStartWatchingInteractor: ObserveStartWatchingInteractor,
    repository: WatchlistPrefsRepository,
    syncObserver: SyncObserver,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")

    init {
        observeStartWatchingInteractor(Unit)
    }

    public val state: StateFlow<StartWatchingState> = combine(
        observeStartWatchingInteractor.flow,
        queryFlow,
        repository.observeSortOption(),
        repository.observeListStyle(),
        syncObserver.isSyncing,
    ) { shows, query, sortOption, isGridMode, isSyncing ->
        StartWatchingState(
            isSyncing = isSyncing,
            isGridMode = isGridMode,
            items = shows.toStartWatchingItems(query, sortOption),
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = StartWatchingState(),
    )

    public val stateValue: Value<StartWatchingState> = state.asValue(coroutineScope)

    public fun onQueryChanged(query: String) {
        queryFlow.value = query
    }

    public fun dispatch(action: StartWatchingAction) {
        when (action) {
            is StartWatchingShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(id = action.traktId)))
        }
    }
}
