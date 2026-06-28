package com.thomaskioko.tvmaniac.presenter.showdetails.cast

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
import com.thomaskioko.tvmaniac.domain.showdetails.FetchCastInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveCastInteractor
import com.thomaskioko.tvmaniac.presenter.showdetails.toCastModels
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
public class ShowDetailsCastPresenter(
    componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val forceRefresh: Boolean,
    observeCastInteractor: ObserveCastInteractor,
    private val fetchCastInteractor: FetchCastInteractor,
    private val accountManager: AccountManager,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    init {
        observeCastInteractor(showId)
        fetchCast(forceRefresh = forceRefresh)
        observeAuthState()
    }

    public val state: StateFlow<ShowDetailsCastState> = combine(
        loadingState.observable,
        observeCastInteractor.flow,
        uiMessageManager.message,
    ) { isLoading, cast, message ->
        ShowDetailsCastState(
            castsList = cast.toCastModels(),
            isRefreshing = isLoading,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ShowDetailsCastState(),
    )

    public val stateValue: Value<ShowDetailsCastState> = state.asValue(coroutineScope)

    public fun refresh() {
        fetchCast(forceRefresh = true)
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
                .collect { fetchCast(forceRefresh = true) }
        }
    }

    private fun fetchCast(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            fetchCastInteractor(FetchCastInteractor.Param(showId, forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Cast", errorToStringMapper)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(showId: Long, forceRefresh: Boolean): ShowDetailsCastPresenter
    }
}
