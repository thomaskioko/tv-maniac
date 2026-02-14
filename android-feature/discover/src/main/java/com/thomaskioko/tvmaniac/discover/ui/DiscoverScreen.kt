package com.thomaskioko.tvmaniac.discover.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CircularCard
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowAction
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverViewState
import com.thomaskioko.tvmaniac.discover.presenter.MarkNextEpisodeWatched
import com.thomaskioko.tvmaniac.discover.presenter.MessageShown
import com.thomaskioko.tvmaniac.discover.presenter.NextEpisodeClicked
import com.thomaskioko.tvmaniac.discover.presenter.OpenSeasonFromUpNext
import com.thomaskioko.tvmaniac.discover.presenter.PopularClicked
import com.thomaskioko.tvmaniac.discover.presenter.ProfileIconClicked
import com.thomaskioko.tvmaniac.discover.presenter.RefreshData
import com.thomaskioko.tvmaniac.discover.presenter.ShowClicked
import com.thomaskioko.tvmaniac.discover.presenter.TopRatedClicked
import com.thomaskioko.tvmaniac.discover.presenter.TrendingClicked
import com.thomaskioko.tvmaniac.discover.presenter.UnfollowShowFromUpNext
import com.thomaskioko.tvmaniac.discover.presenter.UpComingClicked
import com.thomaskioko.tvmaniac.discover.ui.component.DiscoverHeaderContent
import com.thomaskioko.tvmaniac.discover.ui.component.HorizontalRowContent
import com.thomaskioko.tvmaniac.discover.ui.component.NextEpisodesSection
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_empty_content
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_up_next
import com.thomaskioko.tvmaniac.i18n.MR.strings.missing_api_key
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_category_popular
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_category_top_rated
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_category_trending_today
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_category_upcoming
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
public fun DiscoverScreen(
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

    TvManiacSnackBarHost(
        message = discoverState.message?.message,
        style = SnackBarStyle.Error,
        onDismiss = { discoverState.message?.let { MessageShown(it.id) } },
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
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.8F,
                            ),
                        ),
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
    val pullRefreshState =
        rememberPullRefreshState(refreshing = false, onRefresh = { onAction(RefreshData) })
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
            actions = { _ ->
                CircularCard(
                    imageUrl = state.userAvatarUrl,
                    contentDescription = "Profile",
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { onAction(ProfileIconClicked) },
                )
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
            NextEpisodesSection(
                title = label_discover_up_next.resolve(context),
                nextEpisodes = dataLoadedState.nextEpisodes,
                onEpisodeClick = { showId, episodeId ->
                    onAction(NextEpisodeClicked(showId, episodeId))
                },
                onMarkWatched = { episode ->
                    onAction(
                        MarkNextEpisodeWatched(
                            showTraktId = episode.showTraktId,
                            episodeId = episode.episodeId,
                            seasonNumber = episode.seasonNumber,
                            episodeNumber = episode.episodeNumber,
                        ),
                    )
                },
                onUnfollowShow = { showId ->
                    onAction(UnfollowShowFromUpNext(showId))
                },
                onOpenSeason = { showId, seasonId, seasonNumber ->
                    onAction(OpenSeasonFromUpNext(showId, seasonId, seasonNumber))
                },
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
                category = title_category_upcoming.resolve(context),
                tvShows = dataLoadedState.upcomingShows,
                onItemClicked = { onAction(ShowClicked(it)) },
                onMoreClicked = { onAction(UpComingClicked) },
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

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
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
