package com.thomaskioko.tvmaniac.discover.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.ParallaxCarouselImage
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.RefreshButton
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.ScrimButton
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.discover.presenter.AccountClicked
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowAction
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverViewState
import com.thomaskioko.tvmaniac.discover.presenter.MessageShown
import com.thomaskioko.tvmaniac.discover.presenter.PopularClicked
import com.thomaskioko.tvmaniac.discover.presenter.RefreshData
import com.thomaskioko.tvmaniac.discover.presenter.ShowClicked
import com.thomaskioko.tvmaniac.discover.presenter.TopRatedClicked
import com.thomaskioko.tvmaniac.discover.presenter.TrendingClicked
import com.thomaskioko.tvmaniac.discover.presenter.UpComingClicked
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.discover.ui.component.CircularIndicator
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_empty_content
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.MR.strings.missing_api_key
import com.thomaskioko.tvmaniac.i18n.MR.strings.str_more
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_category_popular
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_category_top_rated
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_category_trending_today
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_category_upcoming
import com.thomaskioko.tvmaniac.i18n.resolve
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

@Composable
fun DiscoverScreen(
    presenter: DiscoverShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val discoverState by presenter.state.collectAsState()
    val pagerState = rememberPagerState(
        pageCount = { discoverState.featuredShows.size },
    )
    val snackBarHostState = remember { SnackbarHostState() }
    val dismissSnackbarState = rememberDismissState { value ->
        if (value != DismissValue.Default) {
            snackBarHostState.currentSnackbarData?.dismiss()
            true
        } else {
            false
        }
    }

    DiscoverScreen(
        modifier = modifier,
        state = discoverState,
        snackBarHostState = snackBarHostState,
        dismissSnackbarState = dismissSnackbarState,
        pagerState = pagerState,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun DiscoverScreen(
    state: DiscoverViewState,
    snackBarHostState: SnackbarHostState,
    dismissSnackbarState: DismissState,
    pagerState: PagerState,
    onAction: (DiscoverShowAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                SwipeToDismiss(
                    state = dismissSnackbarState,
                    background = {},
                    dismissContent = { Snackbar(snackbarData = data) },
                )
            }
        },
    ) { paddingValues ->
        val context = LocalContext.current
        when {
            state.isEmpty ->
                EmptyContent(
                    modifier = Modifier
                        .padding(paddingValues.copy(copyBottom = false)),
                    imageVector = Icons.Filled.Movie,
                    title = generic_empty_content.resolve(context),
                    message = missing_api_key.resolve(context),
                    buttonText = generic_retry.resolve(context),
                    onClick = { onAction(RefreshData) },
                )
            state.showError -> ErrorUi(
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
                errorMessage = state.message?.message,
                onRetry = { onAction(RefreshData) },
            )
            else -> DiscoverContent(
                modifier = modifier,
                pagerState = pagerState,
                state = state,
                snackBarHostState = snackBarHostState,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun DiscoverContent(
    state: DiscoverViewState,
    snackBarHostState: SnackbarHostState,
    pagerState: PagerState,
    onAction: (DiscoverShowAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.showSnackBarError) {
        state.message?.let { message ->
            LaunchedEffect(message) {
                snackBarHostState.showSnackbar(message.message)
                // Notify the view model that the message has been dismissed
                onAction(MessageShown(message.id))
            }
        }
    }

    val pullRefreshState = rememberPullRefreshState(refreshing = false, onRefresh = { onAction(RefreshData) })
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
    ) {
        LazyColumnContent(
            modifier = modifier,
            pagerState = pagerState,
            dataLoadedState = state,
            listState = listState,
            onAction = onAction,
        )

        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            scale = true,
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.secondary,
        )

        RefreshCollapsableTopAppBar(
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
            actions = { showScrim ->
                ScrimButton(
                    show = showScrim,
                    onClick = {
                        onAction(AccountClicked)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = state.isRefreshing,
                ) {
                    ScrimButton(
                        show = showScrim,
                        onClick = {
                            onAction(RefreshData)
                        },
                    ) {
                        RefreshButton(
                            modifier = Modifier
                                .size(20.dp),
                            isRefreshing = state.isRefreshing,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            },
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun LazyColumnContent(
    pagerState: PagerState,
    dataLoadedState: DiscoverViewState,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onAction: (DiscoverShowAction) -> Unit,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
        state = listState,
    ) {
        if (dataLoadedState.featuredShows.isEmpty()) {
            item {
                Spacer(modifier = Modifier.padding(top = 108.dp))
            }
        }

        item {
            DiscoverHeaderContent(
                pagerState = pagerState,
                showList = dataLoadedState.featuredShows,
                onShowClicked = { onAction(ShowClicked(it)) },
            )
        }

        item {
            HorizontalRowContent(
                category = title_category_upcoming.resolve(context),
                tvShows = dataLoadedState.upcomingShows,
                onItemClicked = { onAction(ShowClicked(it)) },
                onMoreClicked = { onAction(UpComingClicked) },
            )
        }

        item {
            HorizontalRowContent(
                category = title_category_trending_today.resolve(context),
                tvShows = dataLoadedState.trendingToday,
                onItemClicked = { onAction(ShowClicked(it)) },
                onMoreClicked = { onAction(TrendingClicked) },
            )
        }

        item {
            HorizontalRowContent(
                category = title_category_popular.resolve(context),
                tvShows = dataLoadedState.popularShows,
                onItemClicked = { onAction(ShowClicked(it)) },
                onMoreClicked = { onAction(PopularClicked) },
            )
        }

        item {
            HorizontalRowContent(
                category = title_category_top_rated.resolve(context),
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
        modifier = modifier.windowInsetsPadding(
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
    val memoizedOnClick = remember(onClick) { onClick }
    if (list.isEmpty()) return

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

            val currentShow = remember(list, currentPage) { list[currentPage] }
            ParallaxCarouselImage(
                state = pagerState,
                currentPage = currentPage,
                imageUrl = currentShow.posterImageUrl,
                modifier = Modifier
                    .clickable(onClick = { memoizedOnClick(currentShow.tmdbId) }),
            ) {
                ShowCardOverlay(
                    title = currentShow.title,
                    overview = currentShow.overView,
                )
            }
        }

        if (list.isNotEmpty()) {
            LaunchedEffect(key1 = list.size) {
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
                            easing = FastOutSlowInEasing,
                        ),
                    )
                }
            }

            CircularIndicator(
                modifier = Modifier.align(Alignment.BottomCenter),
                totalItems = list.size,
                currentPage = pagerState.currentPage,
                isUserScrolling = pagerState.isScrollInProgress,
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
                label = str_more.resolve(LocalContext.current),
                onMoreClicked = onMoreClicked,
            )

            val lazyListState = rememberLazyListState()

            LazyRow(
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(
                    items = tvShows,
                    key = { index, tvShow -> "${category}_${tvShow.tmdbId}_$index" },
                ) { index, tvShow ->
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
    @PreviewParameter(DiscoverPreviewParameterProvider::class) state: DiscoverViewState,
) {
    TvManiacTheme {
        TvManiacBackground {
            val pagerState = rememberPagerState(pageCount = { 5 })
            val snackBarHostState = remember { SnackbarHostState() }
            val dismissSnackbarState = rememberDismissState { true }
            DiscoverScreen(
                state = state,
                pagerState = pagerState,
                snackBarHostState = snackBarHostState,
                dismissSnackbarState = dismissSnackbarState,
                onAction = {},
            )
        }
    }
}
