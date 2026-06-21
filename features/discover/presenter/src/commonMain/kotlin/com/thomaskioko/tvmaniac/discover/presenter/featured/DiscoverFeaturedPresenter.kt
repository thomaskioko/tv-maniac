package com.thomaskioko.tvmaniac.discover.presenter.featured

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.discover.nav.scope.DiscoverChildScope
import com.thomaskioko.tvmaniac.discover.presenter.toShowList
import com.thomaskioko.tvmaniac.domain.discover.ObserveFeaturedShowsInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ChildPresenter(scope = DiscoverChildScope::class, parentScope = DiscoverRoot::class)
@Inject
public class DiscoverFeaturedPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val observeFeaturedShowsInteractor: ObserveFeaturedShowsInteractor,
    private val featuredShowsInteractor: FeaturedShowsInteractor,
    private val accountManager: AccountManager,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val _state = MutableStateFlow(DiscoverFeaturedState())

    init {
        observeFeaturedShowsInteractor(Unit)
        fetchFeaturedShows()
        observeAuthState()
    }

    public val state: StateFlow<DiscoverFeaturedState> = combine(
        loadingState.observable,
        observeFeaturedShowsInteractor.flow,
        uiMessageManager.message,
        _state,
    ) { isLoading, shows, message, currentState ->
        currentState.copy(
            isInitial = currentState.isInitial && !isLoading && shows.isEmpty() && message == null,
            loading = isLoading,
            featuredShows = shows.toShowList(),
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = _state.value,
    )

    public val stateValue: Value<DiscoverFeaturedState> = state.asValue(coroutineScope)

    public fun dispatch(action: DiscoverFeaturedAction) {
        when (action) {
            is FeaturedShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
        }
    }

    public fun refresh() {
        fetchFeaturedShows(forceRefresh = true)
    }

    public fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            accountManager.isConnected
                .drop(1)
                .distinctUntilChanged()
                .filter { it }
                .collect { fetchFeaturedShows(forceRefresh = true) }
        }
    }

    private fun fetchFeaturedShows(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            featuredShowsInteractor(forceRefresh)
                .collectStatus(loadingState, logger, uiMessageManager, "Featured Shows", errorToStringMapper)
        }
    }
}
