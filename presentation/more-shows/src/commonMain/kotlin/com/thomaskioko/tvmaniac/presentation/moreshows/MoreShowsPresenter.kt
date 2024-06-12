package com.thomaskioko.tvmaniac.presentation.moreshows

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.Category
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias MoreShowsPresenterFactory =
  (
    ComponentContext,
    id: Long,
    onBack: () -> Unit,
    onNavigateToShowDetails: (id: Long) -> Unit,
  ) -> MoreShowsPresenter

@Inject
class MoreShowsPresenter(
  @Assisted componentContext: ComponentContext,
  @Assisted categoryId: Long,
  @Assisted private val onBack: () -> Unit,
  @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
  private val popularShowsRepository: PopularShowsRepository,
  private val upcomingShowsRepository: UpcomingShowsRepository,
  private val trendingShowsRepository: TrendingShowsRepository,
  private val topRatedShowsRepository: TopRatedShowsRepository,
) : ComponentContext by componentContext {

  private val coroutineScope = coroutineScope()
  private val _state = MutableStateFlow(MoreShowsState())
  val state: StateFlow<MoreShowsState> = _state.asStateFlow()

  init {
    when (categoryId) {
      Category.UPCOMING.id -> getUpcomingPagedList()
      Category.TRENDING_TODAY.id -> getTrendingPagedList()
      Category.POPULAR.id -> getPopularPagedList()
      Category.TOP_RATED.id -> getTopRatedPagedList()
    }
  }

  fun dispatch(action: MoreShowsActions) {
    when (action) {
      MoreBackClicked -> onBack()
      is ShowClicked -> onNavigateToShowDetails(action.showId)
    }
  }

  private fun getPopularPagedList() {
    coroutineScope.launch {
      val pagingList: Flow<PagingData<TvShow>> =
        popularShowsRepository.getPagedPopularShows().mapToTvShow().cachedIn(coroutineScope)

      updateState(pagingList = pagingList, title = Category.POPULAR.title)
    }
  }

  private fun getUpcomingPagedList() {
    coroutineScope.launch {
      val pagingList: Flow<PagingData<TvShow>> =
        upcomingShowsRepository.getPagedUpcomingShows().mapToTvShow().cachedIn(coroutineScope)

      updateState(pagingList = pagingList, title = Category.UPCOMING.title)
    }
  }

  private fun getTrendingPagedList() {
    coroutineScope.launch {
      val pagingList: Flow<PagingData<TvShow>> =
        trendingShowsRepository.getPagedTrendingShows().mapToTvShow().cachedIn(coroutineScope)

      updateState(pagingList = pagingList, title = Category.TRENDING_TODAY.title)
    }
  }

  private fun getTopRatedPagedList() {
    coroutineScope.launch {
      val pagingList: Flow<PagingData<TvShow>> =
        topRatedShowsRepository.getPagedTopRatedShows().mapToTvShow().cachedIn(coroutineScope)

      updateState(pagingList = pagingList, title = Category.TOP_RATED.title)
    }
  }

  private fun updateState(title: String, pagingList: Flow<PagingData<TvShow>>) {
    _state.update {
      it.copy(
        list = pagingList,
        categoryTitle = title,
      )
    }
  }

  private fun Flow<PagingData<ShowEntity>>.mapToTvShow(): Flow<PagingData<TvShow>> = map {
    it.map { show ->
      TvShow(
        tmdbId = show.id,
        title = show.title,
        posterImageUrl = show.posterPath,
        inLibrary = show.inLibrary,
      )
    }
  }
}
