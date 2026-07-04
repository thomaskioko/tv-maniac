package com.thomaskioko.tvmaniac.presenter.showdetails.trailers

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
import com.thomaskioko.tvmaniac.domain.showdetails.FetchTrailersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveTrailersInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presenter.showdetails.toTrailerModels
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.scope.ShowDetailsChildScope
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ChildPresenter(scope = ShowDetailsChildScope::class, parentScope = ShowDetailsRoute::class)
@AssistedInject
public class ShowDetailsTrailersPresenter(
    componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val forceRefresh: Boolean,
    observeTrailersInteractor: ObserveTrailersInteractor,
    private val fetchTrailersInteractor: FetchTrailersInteractor,
    private val navigator: Navigator,
    private val accountManager: AccountManager,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    init {
        observeTrailersInteractor(showId)
        fetchTrailers(forceRefresh = forceRefresh)
        observeAuthState()
    }

    public val state: StateFlow<ShowDetailsTrailersState> = combine(
        loadingState.observable,
        observeTrailersInteractor.flow,
        uiMessageManager.message,
    ) { isLoading, trailers, message ->
        ShowDetailsTrailersState(
            trailersList = trailers.trailers.toTrailerModels(),
            hasWebViewInstalled = trailers.hasWebViewInstalled,
            isRefreshing = isLoading,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ShowDetailsTrailersState(),
    )

    public val stateValue: Value<ShowDetailsTrailersState> = state.asValue(coroutineScope)

    public fun dispatch(action: ShowDetailsTrailersAction) {
        when (action) {
            is ShowDetailsWatchTrailerClicked -> navigator.navigateTo(TrailersRoute(action.id))
        }
    }

    public fun refresh() {
        fetchTrailers(forceRefresh = true)
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
                .collect { fetchTrailers(forceRefresh = true) }
        }
    }

    private fun fetchTrailers(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            fetchTrailersInteractor(FetchTrailersInteractor.Param(showId, forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Trailers", errorToStringMapper)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(showId: Long, forceRefresh: Boolean): ShowDetailsTrailersPresenter
    }
}
