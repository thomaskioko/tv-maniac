package com.thomaskioko.tvmaniac.statistics.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.domain.statistics.ObserveWatchStatisticsInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.statistics.nav.StatisticsRoute
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
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
    private val stateMapper: StatisticsStateMapper,
    private val navigator: Navigator,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val uiMessageManager = UiMessageManager()

    init {
        observeWatchStatisticsInteractor(Unit)
    }

    public val state: StateFlow<StatisticsState> = combine(
        observeWatchStatisticsInteractor.flow,
        subscriptionManager.observeAccess(SubscriptionFeature.Statistics),
        accountManager.activeProvider,
        uiMessageManager.message,
    ) { statistics, hasAccess, activeProvider, message ->
        StatisticsState(
            isLoading = false,
            isLocked = !hasAccess,
            hasWatchHistory = statistics.hasWatchHistory,
            showsMarkedWatchedTimes = activeProvider == SyncProviderSource.SIMKL,
            totalWatchTime = stateMapper.toWatchTime(statistics),
            tiles = stateMapper.toTiles(statistics),
            heatMap = stateMapper.toHeatMap(statistics),
            mostWatchedShows = stateMapper.toMostWatchedShows(statistics),
            watchStatusBreakdown = stateMapper.toWatchStatusBreakdown(statistics),
            ratingBreakdown = stateMapper.toRatingBreakdown(statistics),
            yearlyActivity = stateMapper.toYearlyActivity(statistics),
            monthlyActivity = stateMapper.toMonthlyActivity(statistics),
            weekdayActivity = stateMapper.toWeekdayActivity(statistics),
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
        }
    }
}
