package com.thomaskioko.tvmaniac.presentation.discover

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.shows.api.model.Category
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class DiscoverPresenterFactory(
  val create: (
    componentContext: ComponentContext,
    onNavigateToShowDetails: (id: Long) -> Unit,
    onNavigateToMore: (categoryId: Long) -> Unit,
  ) -> DiscoverShowsPresenter,
)

@Inject
class DiscoverShowsPresenter(
  @Assisted componentContext: ComponentContext,
  @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
  @Assisted private val onNavigateToMore: (Long) -> Unit,
  private val discoverShowsInteractor: DiscoverShowsInteractor,
  private val watchlistRepository: WatchlistRepository,
  private val featuredShowsInteractor: FeaturedShowsInteractor,
  private val topRatedShowsInteractor: TopRatedShowsInteractor,
  private val popularShowsInteractor: PopularShowsInteractor,
  private val trendingShowsInteractor: TrendingShowsInteractor,
  private val upcomingShowsInteractor: UpcomingShowsInteractor,
  private val logger: Logger,
  private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : ComponentContext by componentContext {

  internal val presenterInstance = instanceKeeper.getOrCreate { PresenterInstance() }

  val state: StateFlow<DiscoverViewState> = presenterInstance.state

  init {
    presenterInstance.init()
  }

  fun dispatch(action: DiscoverShowAction) {
    presenterInstance.dispatch(action)
  }

  internal inner class PresenterInstance : InstanceKeeper.Instance {

    private val featuredLoadingState = ObservableLoadingCounter()
    private val topRatedLoadingState = ObservableLoadingCounter()
    private val popularLoadingState = ObservableLoadingCounter()
    private val trendingLoadingState = ObservableLoadingCounter()
    private val upcomingLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    val state: StateFlow<DiscoverViewState> = combine(
      featuredLoadingState.observable,
      topRatedLoadingState.observable,
      popularLoadingState.observable,
      trendingLoadingState.observable,
      upcomingLoadingState.observable,
      discoverShowsInteractor.flow,
      uiMessageManager.message,
    ) { featuredShowsIsUpdating, topRatedShowsIsUpdating, popularShowsIsUpdating,
        trendingShowsIsUpdating, upComingIsUpdating, showData, message ->

      DiscoverViewState(
        message = message,
        featuredRefreshing = featuredShowsIsUpdating,
        topRatedRefreshing = topRatedShowsIsUpdating,
        popularRefreshing = popularShowsIsUpdating,
        trendingRefreshing = topRatedShowsIsUpdating,
        upcomingRefreshing = upComingIsUpdating,
        featuredShows = showData.featuredShows.toShowList(),
        topRatedShows = showData.topRatedShows.toShowList(),
        popularShows = showData.popularShows.toShowList(),
        trendingToday = showData.trendingShows.toShowList(),
        upcomingShows = showData.upcomingShows.toShowList(),
      )
    }.stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = DiscoverViewState.Empty,
    )

    fun init() {
      discoverShowsInteractor(Unit)
      observeShowData()
    }

    fun dispatch(action: DiscoverShowAction) {
      when (action) {
        is ShowClicked -> onNavigateToShowDetails(action.id)
        PopularClicked -> onNavigateToMore(Category.POPULAR.id)
        TopRatedClicked -> onNavigateToMore(Category.TOP_RATED.id)
        TrendingClicked -> onNavigateToMore(Category.TRENDING_TODAY.id)
        UpComingClicked -> onNavigateToMore(Category.UPCOMING.id)
        RefreshData -> observeShowData(forceRefresh = true)
        is UpdateShowInLibrary -> {
          coroutineScope.launch {
            watchlistRepository.updateLibrary(
              id = action.id,
              addToLibrary = !action.inLibrary,
            )
          }
        }
        AccountClicked -> {
          //TODO:: Add implementation.
        }
        is MessageShown -> {
          clearMessage(action.id)
        }
      }
    }

    fun clearMessage(id: Long) {
      coroutineScope.launch {
        uiMessageManager.clearMessage(id)
      }
    }

    private fun observeShowData(forceRefresh: Boolean = false) {

      coroutineScope.launch {
        featuredShowsInteractor(forceRefresh)
          .collectStatus(featuredLoadingState, logger, uiMessageManager)
      }

      coroutineScope.launch {
        topRatedShowsInteractor(forceRefresh)
          .collectStatus(topRatedLoadingState, logger, uiMessageManager)
      }

      coroutineScope.launch {
        popularShowsInteractor(forceRefresh)
          .collectStatus(popularLoadingState, logger, uiMessageManager)
      }

      coroutineScope.launch {
        trendingShowsInteractor(forceRefresh)
          .collectStatus(trendingLoadingState, logger, uiMessageManager)
      }
      coroutineScope.launch {
        upcomingShowsInteractor(forceRefresh)
          .collectStatus(upcomingLoadingState, logger, uiMessageManager)
      }
    }

    override fun onDestroy() {
      coroutineScope.cancel()
    }
  }
}
