package com.thomaskioko.tvmaniac.ui.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.compose.components.TvManiacOutlinedButton
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.extensions.verticalGradientScrim
import com.thomaskioko.tvmaniac.compose.theme.MinContrastOfPrimaryVsSurface
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.contrastAgainst
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.presentation.discover.DataLoaded
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowAction
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverState
import com.thomaskioko.tvmaniac.presentation.discover.EmptyState
import com.thomaskioko.tvmaniac.presentation.discover.ErrorState
import com.thomaskioko.tvmaniac.presentation.discover.Loading
import com.thomaskioko.tvmaniac.presentation.discover.PopularClicked
import com.thomaskioko.tvmaniac.presentation.discover.RefreshData
import com.thomaskioko.tvmaniac.presentation.discover.ReloadData
import com.thomaskioko.tvmaniac.presentation.discover.ShowClicked
import com.thomaskioko.tvmaniac.presentation.discover.SnackBarDismissed
import com.thomaskioko.tvmaniac.presentation.discover.TopRatedClicked
import com.thomaskioko.tvmaniac.presentation.discover.TrendingClicked
import com.thomaskioko.tvmaniac.presentation.discover.UpComingClicked
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlin.math.absoluteValue
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverScreen(
  discoverShowsPresenter: DiscoverShowsPresenter,
  modifier: Modifier = Modifier,
) {
  val discoverState by discoverShowsPresenter.value.subscribeAsState()
  val pagerState =
    rememberPagerState(
      initialPage = 2,
      pageCount = { (discoverState as? DataLoaded)?.featuredShows?.size ?: 0 },
    )
  val snackBarHostState = remember { SnackbarHostState() }

  DiscoverScreen(
    modifier = modifier,
    state = discoverState,
    snackBarHostState = snackBarHostState,
    pagerState = pagerState,
    onAction = discoverShowsPresenter::dispatch,
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DiscoverScreen(
  state: DiscoverState,
  snackBarHostState: SnackbarHostState,
  pagerState: PagerState,
  onAction: (DiscoverShowAction) -> Unit,
  modifier: Modifier = Modifier,
) {
  when (state) {
    Loading ->
      LoadingIndicator(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
      )
    EmptyState ->
      EmptyContent(
        modifier = modifier,
        onAction = onAction,
      )
    is DataLoaded ->
      DiscoverContent(
        modifier = modifier,
        pagerState = pagerState,
        snackBarHostState = snackBarHostState,
        state = state,
        onAction = onAction,
      )
    is ErrorState ->
      ErrorUi(
        errorMessage = state.errorMessage,
        onRetry = { onAction(ReloadData) },
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
      )
  }
}

@Composable
private fun EmptyContent(
  modifier: Modifier = Modifier,
  onAction: (DiscoverShowAction) -> Unit,
) {
  Column(
    modifier = modifier.padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Icon(
      modifier = Modifier.size(180.dp),
      imageVector = Icons.Filled.Movie,
      tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F),
      contentDescription = null,
    )

    Text(
      modifier = Modifier.padding(top = 16.dp),
      text = stringResource(R.string.generic_empty_content),
      style = MaterialTheme.typography.titleLarge,
      textAlign = TextAlign.Center,
    )

    Text(
      modifier = Modifier.padding(top = 4.dp),
      text = stringResource(R.string.missing_api_key),
      style = MaterialTheme.typography.labelMedium,
      textAlign = TextAlign.Center,
    )

    TvManiacOutlinedButton(
      modifier = Modifier.padding(top = 16.dp),
      text = stringResource(id = R.string.generic_retry),
      onClick = { onAction(ReloadData) },
    )
  }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun DiscoverContent(
  state: DataLoaded,
  snackBarHostState: SnackbarHostState,
  pagerState: PagerState,
  onAction: (DiscoverShowAction) -> Unit,
  modifier: Modifier = Modifier,
) {
  LaunchedEffect(key1 = state.errorMessage) {
    state.errorMessage?.let {
      val snackBarResult =
        snackBarHostState.showSnackbar(
          message = it,
          duration = SnackbarDuration.Short,
        )
      when (snackBarResult) {
        SnackbarResult.ActionPerformed,
        SnackbarResult.Dismissed, -> onAction(SnackBarDismissed)
      }
    }
  }

  val pullRefreshState =
    rememberPullRefreshState(refreshing = false, onRefresh = { onAction(RefreshData) })

  Box(
    modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState),
    contentAlignment = Alignment.BottomCenter,
  ) {
    LazyColumn(
      modifier =
        modifier
          .fillMaxSize()
          .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
    ) {
      item {
        DiscoverHeaderContent(
          pagerState = pagerState,
          showList = state.featuredShows,
          onShowClicked = { onAction(ShowClicked(it)) },
        )
      }

      item {
        HorizontalRowContent(
          category = stringResource(id = R.string.title_category_upcoming),
          tvShows = state.upcomingShows,
          onItemClicked = { onAction(ShowClicked(it)) },
          onMoreClicked = { onAction(UpComingClicked) },
        )
      }

      item {
        HorizontalRowContent(
          category = stringResource(id = R.string.title_category_trending_today),
          tvShows = state.trendingToday,
          onItemClicked = { onAction(ShowClicked(it)) },
          onMoreClicked = { onAction(TrendingClicked) },
        )
      }

      item {
        HorizontalRowContent(
          category = stringResource(id = R.string.title_category_popular),
          tvShows = state.popularShows,
          onItemClicked = { onAction(ShowClicked(it)) },
          onMoreClicked = { onAction(PopularClicked) },
        )
      }

      item {
        HorizontalRowContent(
          category = stringResource(id = R.string.title_category_top_rated),
          tvShows = state.topRatedShows,
          onItemClicked = { onAction(ShowClicked(it)) },
          onMoreClicked = { onAction(TopRatedClicked) },
        )
      }
    }

    PullRefreshIndicator(
      refreshing = state.isRefreshing,
      state = pullRefreshState,
      modifier = Modifier.align(Alignment.TopCenter),
      scale = true,
      backgroundColor = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.secondary
    )

    SnackbarHost(hostState = snackBarHostState)
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverHeaderContent(
  showList: ImmutableList<DiscoverShow>,
  pagerState: PagerState,
  modifier: Modifier = Modifier,
  onShowClicked: (Long) -> Unit,
) {
  val selectedImageUrl = showList.getOrNull(pagerState.currentPage)?.posterImageUrl

  DynamicColorContainer(selectedImageUrl) {
    Column(
      modifier =
        modifier.windowInsetsPadding(
          WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
        ),
    ) {
      val backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)

      HorizontalPagerItem(
        list = showList,
        pagerState = pagerState,
        backgroundColor = backgroundColor,
        onClick = onShowClicked,
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun DynamicColorContainer(
  selectedImageUrl: String?,
  content: @Composable () -> Unit,
) {
  val surfaceColor = MaterialTheme.colorScheme.surface
  val dominantColorState = rememberDominantColorState { color ->
    // We want a color which has sufficient contrast against the surface color
    color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
  }

  DynamicThemePrimaryColorsFromImage(dominantColorState) {
    // When the selected image url changes, call updateColorsFromImageUrl() or reset()
    LaunchedEffect(selectedImageUrl) {
      if (selectedImageUrl != null) {
        dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
      } else {
        dominantColorState.reset()
      }
    }

    content()
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerItem(
  list: ImmutableList<DiscoverShow>,
  pagerState: PagerState,
  backgroundColor: Color,
  modifier: Modifier = Modifier,
  onClick: (Long) -> Unit,
) {
  Column(
    modifier =
      modifier
        .windowInsetsPadding(
          WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
        )
        .fillMaxWidth()
        .verticalGradientScrim(
          color = backgroundColor,
          startYPercentage = 1f,
          endYPercentage = 0.5f,
        )
        .padding(top = 84.dp),
  ) {
    HorizontalPager(
      state = pagerState,
      beyondBoundsPageCount = 2,
      contentPadding = PaddingValues(horizontal = 45.dp),
      modifier = Modifier.fillMaxSize(),
    ) { pageNumber ->
      Box(
        modifier =
          Modifier.graphicsLayer {
            val pageOffset =
              ((pagerState.currentPage - pageNumber) + pagerState.currentPageOffsetFraction)
                .absoluteValue

            // We animate the scaleX + scaleY, between 85% and 100%
            lerp(
                start = 0.85f,
                stop = 1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f),
              )
              .also { scale ->
                scaleX = scale
                scaleY = scale
              }

            // We animate the alpha, between 50% and 100%
            alpha =
              lerp(
                start = 0.5f,
                stop = 1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f),
              )
          }
      ) {
        TvPosterCard(
          title = list[pageNumber].title,
          posterImageUrl = list[pageNumber].posterImageUrl,
          onClick = { onClick(list[pageNumber].tmdbId) },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    }

    if (list.isNotEmpty()) {
      LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> pagerState.scrollToPage(page) }
      }

      Row(
        Modifier.height(50.dp)
          .fillMaxWidth()
          .align(Alignment.CenterHorizontally)
          .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center,
      ) {
        repeat(list.size) { iteration ->
          val color =
            if (pagerState.currentPage == iteration) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            }

          Box(
            modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(8.dp),
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalSnapperApi::class, ExperimentalFoundationApi::class)
@Composable
private fun HorizontalRowContent(
  category: String,
  tvShows: ImmutableList<DiscoverShow>,
  onItemClicked: (Long) -> Unit,
  onMoreClicked: () -> Unit,
) {
  AnimatedVisibility(visible = tvShows.isNotEmpty()) {
    Column {
      BoxTextItems(
        title = category,
        label = stringResource(id = R.string.str_more),
        onMoreClicked = onMoreClicked,
      )

      val lazyListState = rememberLazyListState()

      LazyRow(
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
      ) {
        itemsIndexed(tvShows) { index, tvShow ->
          val value = if (index == 0) 16 else 8

          Spacer(modifier = Modifier.width(value.dp))

          TvPosterCard(
            posterImageUrl = tvShow.posterImageUrl,
            title = tvShow.title,
            onClick = { onItemClicked(tvShow.tmdbId) },
            modifier = Modifier.wrapContentHeight().animateItemPlacement(),
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@ThemePreviews
@Composable
private fun DiscoverScreenPreview(
  @PreviewParameter(DiscoverPreviewParameterProvider::class) state: DiscoverState,
) {
  TvManiacTheme {
    TvManiacBackground {
      Surface(Modifier.fillMaxWidth()) {
        val pagerState = rememberPagerState(pageCount = { 5 })
        val snackBarHostState = remember { SnackbarHostState() }
        DiscoverScreen(
          state = state,
          pagerState = pagerState,
          snackBarHostState = snackBarHostState,
          onAction = {},
        )
      }
    }
  }
}
