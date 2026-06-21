package com.thomaskioko.tvmaniac.discover.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.discover.presenter.catalog.DiscoverCatalogPresenter
import com.thomaskioko.tvmaniac.discover.presenter.catalog.di.DiscoverCatalogChildGraph
import com.thomaskioko.tvmaniac.discover.presenter.featured.DiscoverFeaturedPresenter
import com.thomaskioko.tvmaniac.discover.presenter.featured.di.DiscoverFeaturedChildGraph
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.DiscoverStartWatchingPresenter
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.di.DiscoverStartWatchingChildGraph
import com.thomaskioko.tvmaniac.discover.presenter.upnext.DiscoverUpNextPresenter
import com.thomaskioko.tvmaniac.discover.presenter.upnext.di.DiscoverUpNextChildGraph
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.search.nav.SearchRoute
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Inject
@NavDestination(
    route = DiscoverRoot::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.TAB_ROOT,
)
public class DiscoverShowsPresenter(
    componentContext: ComponentContext,
    featuredGraphFactory: DiscoverFeaturedChildGraph.Factory,
    catalogGraphFactory: DiscoverCatalogChildGraph.Factory,
    upNextGraphFactory: DiscoverUpNextChildGraph.Factory,
    startWatchingGraphFactory: DiscoverStartWatchingChildGraph.Factory,
    private val navigator: Navigator,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    public val featuredPresenter: DiscoverFeaturedPresenter =
        featuredGraphFactory.createDiscoverFeaturedGraph(childContext(key = "Featured")).discoverFeaturedPresenter

    public val catalogPresenter: DiscoverCatalogPresenter =
        catalogGraphFactory.createDiscoverCatalogGraph(childContext(key = "Catalog")).discoverCatalogPresenter

    public val upNextPresenter: DiscoverUpNextPresenter =
        upNextGraphFactory.createDiscoverUpNextGraph(childContext(key = "UpNext")).discoverUpNextPresenter

    public val startWatchingPresenter: DiscoverStartWatchingPresenter =
        startWatchingGraphFactory.createDiscoverStartWatchingGraph(childContext(key = "StartWatching"))
            .discoverStartWatchingPresenter

    public val state: StateFlow<DiscoverViewState> = combine(
        featuredPresenter.state,
        catalogPresenter.state,
    ) { featured, catalog ->
        val isRefreshing = featured.isRefreshing || catalog.isRefreshing
        val isEmpty = featured.isEmpty && catalog.isEmpty
        val message = featured.message ?: catalog.message
        DiscoverViewState(
            isRefreshing = isRefreshing,
            isLoading = isRefreshing && isEmpty,
            isEmpty = !isRefreshing && isEmpty,
            showError = message != null && !isRefreshing && isEmpty,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DiscoverViewState.Empty,
    )

    public val stateValue: Value<DiscoverViewState> = state.asValue(coroutineScope)

    public fun dispatch(action: DiscoverShowAction) {
        when (action) {
            SearchIconClicked -> navigator.navigateTo(SearchRoute)
            RefreshData -> {
                featuredPresenter.refresh()
                catalogPresenter.refresh()
            }
            is MessageShown -> {
                featuredPresenter.clearMessage(action.id)
                catalogPresenter.clearMessage(action.id)
            }
        }
    }
}
