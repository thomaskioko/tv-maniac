package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.showdetails.FetchTrailersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveTrailersInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@NavDestination(
    route = TrailersRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@AssistedInject
public class TrailersPresenter internal constructor(
    componentContext: ComponentContext,
    @Assisted private val showId: Long,
    observeTrailersInteractor: ObserveTrailersInteractor,
    private val fetchTrailersInteractor: FetchTrailersInteractor,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) {

    private val coroutineScope = componentContext.coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val title = localizer.getString(StringResourceKey.TitleTrailer)
    private val selectedVideoKey = MutableStateFlow<String?>(null)

    init {
        observeTrailersInteractor(showId)
        fetchTrailers()
    }

    public val state: StateFlow<TrailersState> = combine(
        observeTrailersInteractor.flow,
        loadingState.observable,
        selectedVideoKey,
        uiMessageManager.message,
    ) { result, isLoading, selectedKey, message ->
        when {
            message != null -> TrailerError(
                title = title,
                errorMessage = message.message,
                retryLabel = localizer.getString(StringResourceKey.GenericRetry),
            )
            result.trailers.isEmpty() && isLoading -> LoadingTrailers(title)
            else -> TrailersContent(
                title = title,
                moreTrailersTitle = localizer.getString(StringResourceKey.StrMoreTrailers),
                selectedVideoKey = selectedKey ?: result.trailers.firstOrNull()?.key,
                trailersList = result.trailers.toTrailerList(),
            )
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LoadingTrailers(title),
    )

    public val stateValue: Value<TrailersState> = state.asValue(coroutineScope)

    public fun dispatch(action: TrailersAction) {
        when (action) {
            is TrailerSelected -> selectedVideoKey.value = action.trailerKey
            is VideoPlayerError -> uiMessageManager.emitMessage(UiMessage(action.errorMessage))
            ReloadTrailers -> {
                clearMessage()
                fetchTrailers(forceRefresh = true)
            }
        }
    }

    private fun clearMessage() {
        coroutineScope.launch {
            uiMessageManager.message.first()?.let { uiMessageManager.clearMessage(it.id) }
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
        public fun create(showId: Long): TrailersPresenter
    }
}
