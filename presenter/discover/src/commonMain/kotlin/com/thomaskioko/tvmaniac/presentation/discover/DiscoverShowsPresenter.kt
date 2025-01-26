package com.thomaskioko.tvmaniac.presentation.discover

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.Category
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DiscoverPresenterFactory(
   val create: (
    componentContext: ComponentContext,
    onNavigateToShowDetails: (id: Long) -> Unit,
    onNavigateToMore: (categoryId: Long) -> Unit,
  ) -> DiscoverShowsPresenter
)

@Inject
class DiscoverShowsPresenter(
  @Assisted componentContext: ComponentContext,
  @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
  @Assisted private val onNavigateToMore: (Long) -> Unit,
  private val featuredShowsRepository: FeaturedShowsRepository,
  private val trendingShowsRepository: TrendingShowsRepository,
  private val upcomingShowsRepository: UpcomingShowsRepository,
  private val topRatedShowsRepository: TopRatedShowsRepository,
  private val popularShowsRepository: PopularShowsRepository,
  private val watchlistRepository: WatchlistRepository,
  private val coroutineScope: CoroutineScope = componentContext.coroutineScope()
) : ComponentContext by componentContext {

  internal val presenterInstance = instanceKeeper.getOrCreate { PresenterInstance() }

  val state: StateFlow<DiscoverState> = presenterInstance.state

  init {
    presenterInstance.init()
  }

  fun dispatch(action: DiscoverShowAction) {
    presenterInstance.dispatch(action)
  }

  internal inner class PresenterInstance : InstanceKeeper.Instance {
    private val _state = MutableStateFlow<DiscoverState>(Loading)
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

    fun init() {
      coroutineScope.launch { observeShowData() }
    }

    fun dispatch(action: DiscoverShowAction) {
      when (action) {
        is ShowClicked -> onNavigateToShowDetails(action.id)
        PopularClicked -> onNavigateToMore(Category.POPULAR.id)
        TopRatedClicked -> onNavigateToMore(Category.TOP_RATED.id)
        TrendingClicked -> onNavigateToMore(Category.TRENDING_TODAY.id)
        UpComingClicked -> onNavigateToMore(Category.UPCOMING.id)
        RefreshData ->
          coroutineScope.launch {
            _state.update { (it as? DataLoaded)?.copy(isRefreshing = true) ?: it }
            observeShowData(true)
          }
        ReloadData ->
          coroutineScope.launch {
            _state.update { Loading }
            observeShowData(true)
          }
        SnackBarDismissed ->
          coroutineScope.launch {
            _state.update { state ->
              (state as? DataLoaded)?.copy(
                errorMessage = null,
              )
                ?: state
            }
          }
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
      }
    }

    private fun isEmpty(vararg responses: List<ShowEntity>?): Boolean {
      return responses.all { it.isNullOrEmpty() }
    }

    private suspend fun observeShowData(refresh: Boolean = false) {
      combine(
          featuredShowsRepository.observeFeaturedShows(forceRefresh = refresh),
          topRatedShowsRepository.observeTopRatedShows(forceRefresh = refresh),
          popularShowsRepository.observePopularShows(forceRefresh = refresh),
          upcomingShowsRepository.observeUpcomingShows(forceRefresh = refresh),
          trendingShowsRepository.observeTrendingShows(forceRefresh = refresh),
        ) { featured, topRated, popular, upcomingShows, trendingToday ->
          if (
            isEmpty(
              featured.getOrNull(),
              topRated.getOrNull(),
              popular.getOrNull(),
              upcomingShows.getOrNull(),
              trendingToday.getOrNull(),
            )
          ) {
            _state.update { EmptyState }
          } else {
            _state.update {
              DataLoaded(
                featuredShows = featured.getOrNull().toShowList(),
                topRatedShows = topRated.getOrNull().toShowList(),
                popularShows = popular.getOrNull().toShowList(),
                upcomingShows = upcomingShows.getOrNull().toShowList(),
                trendingToday = trendingToday.getOrNull().toShowList(),
                errorMessage = getErrorMessage(topRated, popular, upcomingShows, featured),
              )
            }
          }
        }
        .onStart { Loading }
        .catch { ErrorState(errorMessage = it.message) }
        .collect()
    }

    override fun onDestroy() {
      coroutineScope.cancel()
    }
  }
}
