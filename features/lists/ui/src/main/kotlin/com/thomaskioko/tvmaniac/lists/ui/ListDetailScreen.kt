package com.thomaskioko.tvmaniac.lists.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.InlineSectionError
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacAlertDialog
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.ImageDimens
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.compose.theme.LocalPosterCornerRadius
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_cancel
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_ok
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailPresenter
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailState
import com.thomaskioko.tvmaniac.lists.presenter.model.ListShow
import com.thomaskioko.tvmaniac.testtags.lists.ListDetailTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi

private const val LOADING_PLACEHOLDER_COUNT = 6

@ScreenUi(presenter = ListDetailPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun ListDetailScreen(
    presenter: ListDetailPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    ListDetailScreen(
        state = state,
        onAction = presenter::dispatch,
        modifier = modifier,
    )
}

@Composable
internal fun ListDetailScreen(
    state: ListDetailState,
    onAction: (ListDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier.testTag(ListDetailTestTags.SCREEN_TEST_TAG),
        topBar = {
            TvManiacTopBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = { onAction(ListDetailAction.BackClicked) })
                            .padding(TvManiacSpacing.medium)
                            .testTag(ListDetailTestTags.BACK_BUTTON_TEST_TAG),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = cd_back.resolve(context),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                },
                title = {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = TvManiacSpacing.medium),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { contentPadding ->
        if (state.canRefresh) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(ListDetailAction.RefreshList) },
                modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
            ) {
                ListDetailBody(
                    state = state,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                    onAction = onAction,
                )
            }
        } else {
            ListDetailBody(
                state = state,
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding,
                onAction = onAction,
            )
        }
    }

    state.removeConfirmation?.let { confirmation ->
        TvManiacAlertDialog(
            title = confirmation.title,
            message = confirmation.message,
            confirmButtonText = confirmation.confirmLabel,
            dismissButtonText = label_cancel.resolve(context),
            onConfirm = { onAction(ListDetailAction.RemoveConfirmed) },
            onDismiss = { onAction(ListDetailAction.RemoveDismissed) },
            confirmButtonTestTag = ListDetailTestTags.REMOVE_CONFIRM_BUTTON_TEST_TAG,
            dismissButtonTestTag = ListDetailTestTags.REMOVE_CANCEL_BUTTON_TEST_TAG,
        )
    }
}

@Composable
private fun ListDetailBody(
    state: ListDetailState,
    lazyPagingItems: LazyPagingItems<ListShow>,
    contentPadding: PaddingValues,
    onAction: (ListDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val errorMessage = state.errorMessage
    when {
        errorMessage != null -> {
            EmptyStateView(
                title = errorMessage,
                imageVector = Icons.Outlined.Warning,
                buttonText = label_ok.resolve(context),
                buttonTestTag = ListDetailTestTags.DISMISS_ERROR_BUTTON_TEST_TAG,
                onClick = { onAction(ListDetailAction.DismissErrorMessage) },
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }

        lazyPagingItems.loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
            ListDetailLoadingGrid(
                contentPadding = contentPadding,
                modifier = modifier,
            )
        }

        lazyPagingItems.itemCount == 0 -> {
            EmptyStateView(
                title = state.emptyMessage,
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }

        else -> {
            ListDetailGrid(
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding,
                appendError = state.appendError,
                onAction = onAction,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ListDetailLoadingGrid(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(Layout.posterColumns),
        verticalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
        horizontalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = ImageDimens.GridItemSpacing),
    ) {
        items(LOADING_PLACEHOLDER_COUNT) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ImageType.Poster.aspect),
                shape = RoundedCornerShape(LocalPosterCornerRadius.current),
            )
        }
    }
}

@Composable
private fun ListDetailGrid(
    lazyPagingItems: LazyPagingItems<ListShow>,
    contentPadding: PaddingValues,
    appendError: String?,
    onAction: (ListDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(Layout.posterColumns),
        verticalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
        horizontalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = ImageDimens.GridItemSpacing)
            .testTag(ListDetailTestTags.GRID_TEST_TAG),
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.tmdbId },
            contentType = lazyPagingItems.itemContentType { "show" },
        ) { index ->
            val show = lazyPagingItems[index]
            if (show != null) {
                PosterCard(
                    imageUrl = show.posterUrl,
                    onClick = { onAction(ListDetailAction.ShowClicked(show.tmdbId)) },
                    onLongClick = { onAction(ListDetailAction.RemoveRequested(show.tmdbId)) },
                    title = show.title,
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .testTag(ListDetailTestTags.showCard(show.tmdbId)),
                )
            }
        }

        when (lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(TvManiacSpacing.large),
                    ) {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                                .padding(TvManiacSpacing.large),
                        )
                    }
                }
            }

            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    InlineSectionError(
                        message = appendError.orEmpty(),
                        retryLabel = generic_retry.resolve(context),
                        onRetry = { onAction(ListDetailAction.RetryLoadMore) },
                    )
                }
            }

            else -> Unit
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ListDetailScreenPreview(
    @PreviewParameter(ListDetailPreviewParameterProvider::class) state: ListDetailState,
) {
    ListDetailScreen(
        state = state,
        onAction = {},
    )
}
