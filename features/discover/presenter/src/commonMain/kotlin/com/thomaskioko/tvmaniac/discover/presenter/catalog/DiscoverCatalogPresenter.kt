package com.thomaskioko.tvmaniac.discover.presenter.catalog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.discover.nav.scope.DiscoverChildScope
import com.thomaskioko.tvmaniac.discover.presenter.toShowList
import com.thomaskioko.tvmaniac.domain.discover.ObservePopularShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveTopRatedShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveTrendingShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveUpcomingShowsInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.shows.api.model.Category
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ChildPresenter(scope = DiscoverChildScope::class, parentScope = DiscoverRoot::class)
@Inject
public class DiscoverCatalogPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val observeTrendingShowsInteractor: ObserveTrendingShowsInteractor,
    private val observeUpcomingShowsInteractor: ObserveUpcomingShowsInteractor,
    private val observePopularShowsInteractor: ObservePopularShowsInteractor,
    private val observeTopRatedShowsInteractor: ObserveTopRatedShowsInteractor,
    private val trendingShowsInteractor: TrendingShowsInteractor,
    private val upcomingShowsInteractor: UpcomingShowsInteractor,
    private val popularShowsInteractor: PopularShowsInteractor,
    private val topRatedShowsInteractor: TopRatedShowsInteractor,
    private val genreShowsInteractor: GenreShowsInteractor,
    private val accountManager: AccountManager,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val trendingLoadingState = ObservableLoadingCounter()
    private val upcomingLoadingState = ObservableLoadingCounter()
    private val popularLoadingState = ObservableLoadingCounter()
    private val topRatedLoadingState = ObservableLoadingCounter()
    private val genreLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val _state = MutableStateFlow(DiscoverCatalogState())

    init {
        observeTrendingShowsInteractor(Unit)
        observeUpcomingShowsInteractor(Unit)
        observePopularShowsInteractor(Unit)
        observeTopRatedShowsInteractor(Unit)
        fetchShows()
        observeAuthState()
    }

    public val state: StateFlow<DiscoverCatalogState> = combine(
        trendingLoadingState.observable,
        upcomingLoadingState.observable,
        popularLoadingState.observable,
        topRatedLoadingState.observable,
        observeTrendingShowsInteractor.flow,
        observeUpcomingShowsInteractor.flow,
        observePopularShowsInteractor.flow,
        observeTopRatedShowsInteractor.flow,
        uiMessageManager.message,
        _state,
    ) { trendingUpdating, upcomingUpdating, popularUpdating, topRatedUpdating,
        trending, upcoming, popular, topRated, message, currentState,
        ->
        val isLoading = trendingUpdating || upcomingUpdating || popularUpdating || topRatedUpdating
        val hasData = trending.isNotEmpty() || upcoming.isNotEmpty() ||
            popular.isNotEmpty() || topRated.isNotEmpty()
        currentState.copy(
            isInitial = currentState.isInitial && !isLoading && !hasData && message == null,
            loading = isLoading,
            trendingShows = trending.toShowList(),
            upcomingShows = upcoming.toShowList(),
            popularShows = popular.toShowList(),
            topRatedShows = topRated.toShowList(),
            trendingTitle = localizer.getString(StringResourceKey.LabelDiscoverTrendingToday),
            upcomingTitle = localizer.getString(StringResourceKey.LabelDiscoverUpcoming),
            popularTitle = localizer.getString(StringResourceKey.LabelDiscoverPopular),
            topRatedTitle = localizer.getString(StringResourceKey.LabelDiscoverTopRated),
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = _state.value,
    )

    public val stateValue: Value<DiscoverCatalogState> = state.asValue(coroutineScope)

    public fun dispatch(action: DiscoverCatalogAction) {
        when (action) {
            is CatalogShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            TrendingMoreClicked -> navigator.navigateTo(MoreShowsRoute(Category.TRENDING_TODAY.id))
            UpcomingMoreClicked -> navigator.navigateTo(MoreShowsRoute(Category.UPCOMING.id))
            PopularMoreClicked -> navigator.navigateTo(MoreShowsRoute(Category.POPULAR.id))
            TopRatedMoreClicked -> navigator.navigateTo(MoreShowsRoute(Category.TOP_RATED.id))
        }
    }

    public fun refresh() {
        fetchShows(forceRefresh = true)
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
                .collect { fetchShows(forceRefresh = true) }
        }
    }

    private fun fetchShows(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            genreShowsInteractor(forceRefresh)
                .collectStatus(genreLoadingState, logger, uiMessageManager, "Genres", errorToStringMapper)
        }
        coroutineScope.launch {
            trendingShowsInteractor(forceRefresh)
                .collectStatus(trendingLoadingState, logger, uiMessageManager, "Trending Shows", errorToStringMapper)
        }
        coroutineScope.launch {
            upcomingShowsInteractor(forceRefresh)
                .collectStatus(upcomingLoadingState, logger, uiMessageManager, "Upcoming Shows", errorToStringMapper)
        }
        coroutineScope.launch {
            popularShowsInteractor(forceRefresh)
                .collectStatus(popularLoadingState, logger, uiMessageManager, "Popular Shows", errorToStringMapper)
        }
        coroutineScope.launch {
            topRatedShowsInteractor(forceRefresh)
                .collectStatus(topRatedLoadingState, logger, uiMessageManager, "Top Rated Shows", errorToStringMapper)
        }
    }
}
