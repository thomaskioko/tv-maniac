package com.thomaskioko.tvmaniac.presenter.showdetails.similar

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
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveSimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presenter.showdetails.toShowModels
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.showdetails.nav.scope.ShowDetailsChildScope
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
public class ShowDetailsSimilarPresenter(
    componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val forceRefresh: Boolean,
    observeSimilarShowsInteractor: ObserveSimilarShowsInteractor,
    private val similarShowsInteractor: SimilarShowsInteractor,
    private val navigator: Navigator,
    private val accountManager: AccountManager,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    init {
        observeSimilarShowsInteractor(showId)
        fetchSimilarShows(forceRefresh = forceRefresh)
        observeAuthState()
    }

    public val state: StateFlow<ShowDetailsSimilarState> = combine(
        loadingState.observable,
        observeSimilarShowsInteractor.flow,
        uiMessageManager.message,
    ) { isLoading, shows, message ->
        ShowDetailsSimilarState(
            similarShows = shows.toShowModels(),
            isRefreshing = isLoading,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ShowDetailsSimilarState(),
    )

    public val stateValue: Value<ShowDetailsSimilarState> = state.asValue(coroutineScope)

    public fun dispatch(action: ShowDetailsSimilarAction) {
        when (action) {
            is ShowDetailsSimilarShowClicked ->
                navigator.pushToFront(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
        }
    }

    public fun refresh() {
        fetchSimilarShows(forceRefresh = true)
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
                .collect { fetchSimilarShows(forceRefresh = true) }
        }
    }

    private fun fetchSimilarShows(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            similarShowsInteractor(SimilarShowsInteractor.Param(showId, forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Similar Shows", errorToStringMapper)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(showId: Long, forceRefresh: Boolean): ShowDetailsSimilarPresenter
    }
}
