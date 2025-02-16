package com.thomaskioko.tvmaniac.ui.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.CollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ParallaxCarouselImage
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.discover.AccountClicked
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
import com.thomaskioko.tvmaniac.android.resources.R
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

@Composable
fun DiscoverScreen(
  presenter: DiscoverShowsPresenter,
  modifier: Modifier = Modifier,
) {
  val discoverState by presenter.state.collectAsState()
  val pagerState =
    rememberPagerState(
      pageCount = { (discoverState as? DataLoaded)?.featuredShows?.size ?: 0 },
    )
  val snackBarHostState = remember { SnackbarHostState() }

  DiscoverScreen(
    modifier = modifier,
    state = discoverState,
    snackBarHostState = snackBarHostState,
    pagerState = pagerState,
    onAction = presenter::dispatch,
  )
}

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
        modifier = Modifier
          .fillMaxSize()
          .wrapContentSize(Alignment.Center),
      )
    EmptyState ->
      EmptyContent(
        modifier = modifier,
        imageVector = Icons.Filled.Movie,
        title = stringResource(R.string.generic_empty_content),
        message = stringResource(R.string.missing_api_key),
        buttonText = stringResource(id = R.string.generic_retry),
        onClick = { onAction(ReloadData) },
      )
    is DataLoaded ->
      DiscoverContent(
        modifier = modifier,
        pagerState = pagerState,
        snackBarHostState = snackBarHostState,
        dataLoadedState = state,
        onAction = onAction,
      )
    is ErrorState ->
      ErrorUi(
        modifier = Modifier
          .fillMaxSize()
          .wrapContentSize(Alignment.Center),
        errorIcon = {
          Image(
            modifier = Modifier.size(120.dp),
            imageVector = Icons.Outlined.ErrorOutline,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F)),
            contentDescription = null,
          )
        },
        errorMessage = state.errorMessage,
        onRetry = { onAction(ReloadData) },
      )
  }
}

@Composable
private fun DiscoverContent(
  dataLoadedState: DataLoaded,
  snackBarHostState: SnackbarHostState,
  pagerState: PagerState,
  onAction: (DiscoverShowAction) -> Unit,
  modifier: Modifier = Modifier,
) {
  LaunchedEffect(key1 = dataLoadedState.errorMessage) {
    dataLoadedState.errorMessage?.let {
      val snackBarResult =
        snackBarHostState.showSnackbar(
          message = it,
          duration = SnackbarDuration.Short,
        )
      when (snackBarResult) {
        SnackbarResult.ActionPerformed,
        SnackbarResult.Dismissed,
          -> onAction(SnackBarDismissed)
      }
    }
  }

  val pullRefreshState = rememberPullRefreshState(refreshing = false, onRefresh = { onAction(RefreshData) })
  val listState = rememberLazyListState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .pullRefresh(pullRefreshState)
  ) {
    LazyColumnContent(
      modifier = modifier,
      pagerState = pagerState,
      dataLoadedState = dataLoadedState,
      listState = listState,
      onAction = onAction,
    )

    PullRefreshIndicator(
      refreshing = dataLoadedState.isRefreshing,
      state = pullRefreshState,
      modifier = Modifier
        .align(Alignment.TopCenter)
        .statusBarsPadding(),
      scale = true,
      backgroundColor = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.secondary,
    )

    CollapsableTopAppBar(
      listState = listState,
      title = {
        Text(
          text = "Discover",
          style = MaterialTheme.typography.titleLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        )
      },
      primaryActionIcon = {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onBackground,
        )
      },
      onPrimaryIconPressed = {
       onAction(AccountClicked)
      }
    )

    SnackbarHost(hostState = snackBarHostState)
  }
}

@Composable
private fun LazyColumnContent(
  pagerState: PagerState,
  dataLoadedState: DataLoaded,
  listState: LazyListState,
  modifier: Modifier = Modifier,
  onAction: (DiscoverShowAction) -> Unit,
) {
  LazyColumn(
    modifier =
      modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
    state = listState,
  ) {
    item {
      DiscoverHeaderContent(
        pagerState = pagerState,
        showList = dataLoadedState.featuredShows,
        onShowClicked = { onAction(ShowClicked(it)) },
      )
    }

    item {
      HorizontalRowContent(
        category = stringResource(id = R.string.title_category_upcoming),
        tvShows = dataLoadedState.upcomingShows,
        onItemClicked = { onAction(ShowClicked(it)) },
        onMoreClicked = { onAction(UpComingClicked) },
      )
    }

    item {
      HorizontalRowContent(
        category = stringResource(id = R.string.title_category_trending_today),
        tvShows = dataLoadedState.trendingToday,
        onItemClicked = { onAction(ShowClicked(it)) },
        onMoreClicked = { onAction(TrendingClicked) },
      )
    }

    item {
      HorizontalRowContent(
        category = stringResource(id = R.string.title_category_popular),
        tvShows = dataLoadedState.popularShows,
        onItemClicked = { onAction(ShowClicked(it)) },
        onMoreClicked = { onAction(PopularClicked) },
      )
    }

    item {
      HorizontalRowContent(
        category = stringResource(id = R.string.title_category_top_rated),
        tvShows = dataLoadedState.topRatedShows,
        onItemClicked = { onAction(ShowClicked(it)) },
        onMoreClicked = { onAction(TopRatedClicked) },
      )
    }
  }
}

@Composable
fun DiscoverHeaderContent(
  showList: ImmutableList<DiscoverShow>,
  pagerState: PagerState,
  modifier: Modifier = Modifier,
  onShowClicked: (Long) -> Unit,
) {

  Column(
    modifier =
      modifier.windowInsetsPadding(
        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
      ),
  ) {
    PosterCardsPager(
      pagerState = pagerState,
      list = showList,
      onClick = onShowClicked,
    )
  }
}


@Composable
fun PosterCardsPager(
  pagerState: PagerState,
  list: ImmutableList<DiscoverShow>,
  modifier: Modifier = Modifier,
  onClick: (Long) -> Unit,
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val pagerHeight = screenHeight / 1.5f
  Box {
    HorizontalPager(
      modifier = modifier
        .fillMaxWidth()
        .height(pagerHeight),
      state = pagerState,
      verticalAlignment = Alignment.Bottom,
    ) { currentPage ->

      ParallaxCarouselImage(
        state = pagerState,
        currentPage = currentPage,
        imageUrl = list[currentPage].posterImageUrl,
        modifier = Modifier
          .clickable(onClick = { onClick(list[currentPage].tmdbId) }),
      ) {
        ShowCardOverlay(
          title = list[currentPage].title,
          overview = list[currentPage].overView,
        )
      }
    }

    if (list.isNotEmpty()) {
      LaunchedEffect(Unit) {
        while (true) {
          delay(4_500)

          // Animate to next page with custom animation spec
          val nextPage = if (pagerState.currentPage + 1 < list.size) {
            pagerState.currentPage + 1
          } else {
            0
          }

          pagerState.animateScrollToPage(
            page = nextPage,
            animationSpec = tween(
              durationMillis = 800,
              easing = FastOutSlowInEasing
            )
          )
        }
      }

      CircularIndicator(
        modifier = Modifier
          .align(Alignment.BottomCenter),
        size = list.size,
        currentPage = pagerState.currentPage,
      )
    }
  }
}

@Composable
private fun CircularIndicator(
  size: Int,
  currentPage: Int,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(bottom = 8.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    repeat(size) { iteration ->
      val color = if (currentPage == iteration) MaterialTheme.colorScheme.onSecondary else Color.Gray
      val size = if (currentPage == iteration) 10.dp else 6.dp

      Box(
        modifier = Modifier
          .padding(2.dp)
          .clip(CircleShape)
          .size(size)
          .background(color),
      )
    }
  }
}

@Composable
private fun ShowCardOverlay(
  title: String,
  overview: String?,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
          startY = 500f,
          endY = 1000f,
        ),
      ),
  ) {
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = -(20).dp)
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {

      Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSecondary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
      )

      Spacer(modifier = Modifier.height(8.dp))

      overview?.let {
        ExpandingText(
          text = overview,
          textStyle = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSecondary,
        )
      }
    }
  }
}


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
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp),
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

          PosterCard(
            imageUrl = tvShow.posterImageUrl,
            title = tvShow.title,
            onClick = { onItemClicked(tvShow.tmdbId) },
          )
        }
      }
    }
  }
}

@ThemePreviews
@Composable
private fun DiscoverScreenPreview(
  @PreviewParameter(DiscoverPreviewParameterProvider::class) state: DiscoverState,
) {
  TvManiacTheme {
    TvManiacBackground {
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
