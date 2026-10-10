package com.thomaskioko.tvmaniac.genreshows.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.thomaskioko.tvmaniac.compose.components.InlineSectionError
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.ImageDimens
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.genreshows.presentation.DismissGenreShowsError
import com.thomaskioko.tvmaniac.genreshows.presentation.GenreShow
import com.thomaskioko.tvmaniac.genreshows.presentation.GenreShowClicked
import com.thomaskioko.tvmaniac.genreshows.presentation.GenreShowsAction
import com.thomaskioko.tvmaniac.genreshows.presentation.GenreShowsBackClicked
import com.thomaskioko.tvmaniac.genreshows.presentation.GenreShowsPresenter
import com.thomaskioko.tvmaniac.genreshows.presentation.GenreShowsState
import com.thomaskioko.tvmaniac.genreshows.presentation.RefreshGenreShows
import com.thomaskioko.tvmaniac.genreshows.presentation.RetryGenreShowsLoadMore
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.testtags.genreshows.GenreShowsTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = GenreShowsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun GenreShowsScreen(
    presenter: GenreShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    GenreShowsScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun GenreShowsScreen(
    state: GenreShowsState,
    onAction: (GenreShowsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pagedList = state.pagingDataFlow.collectAsLazyPagingItems()

    val refreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = { onAction(RefreshGenreShows) },
    )

    Scaffold(
        modifier = modifier
            .statusBarsPadding()
            .testTag(GenreShowsTestTags.SCREEN_TEST_TAG),
        topBar = {
            TvManiacTopBar(
                title = {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                    )
                },
                navigationIcon = {
                    Image(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .clickable(onClick = { onAction(GenreShowsBackClicked) })
                            .padding(16.dp),
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { contentPadding ->
        GridContent(
            contentPadding = contentPadding,
            lazyPagingItems = pagedList,
            scrollBehavior = scrollBehavior,
            refreshState = refreshState,
            refreshing = state.isRefreshLoading,
            appendError = state.appendError,
            onAction = onAction,
        )

        TvManiacSnackBarHost(
            modifier = Modifier.padding(contentPadding),
            message = state.errorMessage,
            style = SnackBarStyle.Error,
            onDismiss = { onAction(DismissGenreShowsError) },
        )
    }
}

@Composable
internal fun GridContent(
    lazyPagingItems: LazyPagingItems<GenreShow>,
    scrollBehavior: TopAppBarScrollBehavior,
    contentPadding: PaddingValues,
    refreshing: Boolean,
    refreshState: PullRefreshState,
    appendError: String?,
    onAction: (GenreShowsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val listState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state = refreshState),
        contentAlignment = Alignment.Center,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(Layout.posterColumns),
            verticalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
            horizontalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
            state = listState,
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(contentPadding)
                .padding(horizontal = ImageDimens.GridItemSpacing)
                .fillMaxHeight()
                .testTag(GenreShowsTestTags.GRID_TEST_TAG),
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = { index -> index },
                contentType = { lazyPagingItems[it] },
            ) { index ->
                val show = lazyPagingItems[index]
                show?.let {
                    PosterCard(
                        imageUrl = show.posterImageUrl,
                        onClick = { onAction(GenreShowClicked(show.showId)) },
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .testTag(GenreShowsTestTags.showCard(show.showId)),
                        title = show.title,
                        isInLibrary = show.inLibrary,
                    )
                }
            }

            if (lazyPagingItems.loadState.append == LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    ) {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                        )
                    }
                }
            }

            if (lazyPagingItems.loadState.append is LoadState.Error) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    InlineSectionError(
                        message = appendError.orEmpty(),
                        retryLabel = generic_retry.resolve(context),
                        onRetry = { onAction(RetryGenreShowsLoadMore) },
                        retryModifier = Modifier.testTag(GenreShowsTestTags.RETRY_TEST_TAG),
                    )
                }
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(contentPadding),
            scale = true,
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.secondary,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun GenreShowsScreenLoadedPreview(
    @PreviewParameter(GenreShowsPreviewParameterProvider::class) state: GenreShowsState,
) {
    GenreShowsScreen(
        state = state,
        onAction = {},
    )
}
