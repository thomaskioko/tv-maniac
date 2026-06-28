package com.thomaskioko.tvmaniac.presenter.showdetails.providers

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
import com.thomaskioko.tvmaniac.domain.showdetails.FetchWatchProvidersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveWatchProvidersInteractor
import com.thomaskioko.tvmaniac.presenter.showdetails.toProviderModels
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
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
public class ShowDetailsProvidersPresenter(
    componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val forceRefresh: Boolean,
    observeWatchProvidersInteractor: ObserveWatchProvidersInteractor,
    private val fetchWatchProvidersInteractor: FetchWatchProvidersInteractor,
    private val accountManager: AccountManager,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    init {
        observeWatchProvidersInteractor(showId)
        fetchProviders(forceRefresh = forceRefresh)
        observeAuthState()
    }

    public val state: StateFlow<ShowDetailsProvidersState> = combine(
        loadingState.observable,
        observeWatchProvidersInteractor.flow,
        uiMessageManager.message,
    ) { isLoading, providers, message ->
        ShowDetailsProvidersState(
            providers = providers.toProviderModels(),
            isRefreshing = isLoading,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ShowDetailsProvidersState(),
    )

    public val stateValue: Value<ShowDetailsProvidersState> = state.asValue(coroutineScope)

    public fun refresh() {
        fetchProviders(forceRefresh = true)
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
                .collect { fetchProviders(forceRefresh = true) }
        }
    }

    private fun fetchProviders(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            fetchWatchProvidersInteractor(FetchWatchProvidersInteractor.Param(showId, forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Watch Providers", errorToStringMapper)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(showId: Long, forceRefresh: Boolean): ShowDetailsProvidersPresenter
    }
}
