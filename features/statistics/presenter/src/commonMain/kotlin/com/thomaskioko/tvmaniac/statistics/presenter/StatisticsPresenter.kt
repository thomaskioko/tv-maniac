package com.thomaskioko.tvmaniac.statistics.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.statistics.ObserveWatchStatisticsInteractor
import com.thomaskioko.tvmaniac.domain.statistics.SyncStatisticsInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.statistics.nav.StatisticsRoute
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@NavDestination(
    route = StatisticsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class StatisticsPresenter internal constructor(
    componentContext: ComponentContext,
    observeWatchStatisticsInteractor: ObserveWatchStatisticsInteractor,
    accountManager: AccountManager,
    subscriptionManager: SubscriptionManager,
    private val syncStatisticsInteractor: SyncStatisticsInteractor,
    private val stateMapper: StatisticsStateMapper,
    private val navigator: Navigator,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val uiMessageManager = UiMessageManager()
    private val refreshingState = ObservableLoadingCounter()

    init {
        observeWatchStatisticsInteractor(Unit)
    }

    public val state: StateFlow<StatisticsState> = combine(
        observeWatchStatisticsInteractor.flow,
        subscriptionManager.observeAccess(SubscriptionFeature.Statistics),
        accountManager.activeProvider,
        uiMessageManager.message,
        refreshingState.observable,
    ) { statistics, hasAccess, activeProvider, message, isRefreshing ->
        StatisticsState(
            isLoading = false,
            isRefreshing = isRefreshing,
            isLocked = !hasAccess,
            hasWatchHistory = statistics.hasWatchHistory,
            showsMarkedWatchedTimes = activeProvider == SyncProviderSource.SIMKL,
            totalWatchTime = stateMapper.toWatchTime(statistics),
            tiles = stateMapper.toTiles(statistics),
            heatMap = stateMapper.toHeatMap(statistics),
            mostWatchedShows = stateMapper.toMostWatchedShows(statistics),
            highestRatedShows = stateMapper.toHighestRatedShows(statistics),
            watchStatusBreakdown = stateMapper.toWatchStatusBreakdown(statistics),
            ratingBreakdown = stateMapper.toRatingBreakdown(statistics),
            yearlyActivity = stateMapper.toYearlyActivity(statistics),
            monthlyActivity = stateMapper.toMonthlyActivity(statistics),
            weekdayActivity = stateMapper.toWeekdayActivity(statistics),
            genreBreakdown = stateMapper.toGenreBreakdown(statistics),
            releaseYears = stateMapper.toReleaseYears(statistics),
            labels = stateMapper.toLabels(),
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = StatisticsState(labels = stateMapper.toLabels()),
    )

    public val stateValue: Value<StatisticsState> = state.asValue(coroutineScope)

    public fun dispatch(action: StatisticsAction) {
        when (action) {
            is StatisticsAction.BackClicked -> navigator.navigateBack()
            is StatisticsAction.UpgradeClicked -> Unit
            is StatisticsAction.ShowClicked -> {
                if (state.value.isLocked) return
                navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            }
            is StatisticsAction.MessageShown -> coroutineScope.launch {
                uiMessageManager.clearMessage(action.id)
            }
            is StatisticsAction.Refresh -> startRefresh()
        }
    }

    /**
     * Suspends until the refresh finishes so a caller can hold a progress indicator open for it.
     * The work runs in the presenter's own scope, so leaving the screen part way through cancels
     * the wait rather than the sync.
     */
    public suspend fun refresh() {
        startRefresh()?.join()
    }

    private fun startRefresh(): Job? {
        if (state.value.isLocked) return null
        return coroutineScope.launch {
            syncStatisticsInteractor(SyncStatisticsInteractor.Params(forceRefresh = true))
                .collectStatus(refreshingState, logger, uiMessageManager, "Statistics", errorToStringMapper)
        }
    }
}
