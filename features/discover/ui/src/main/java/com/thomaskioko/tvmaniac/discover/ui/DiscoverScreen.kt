package com.thomaskioko.tvmaniac.discover.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.ScrimButton
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowAction
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverViewState
import com.thomaskioko.tvmaniac.discover.presenter.MessageShown
import com.thomaskioko.tvmaniac.discover.presenter.RefreshData
import com.thomaskioko.tvmaniac.discover.presenter.SearchIconClicked
import com.thomaskioko.tvmaniac.discover.ui.section.DiscoverCatalogSection
import com.thomaskioko.tvmaniac.discover.ui.section.DiscoverFeaturedSection
import com.thomaskioko.tvmaniac.discover.ui.section.DiscoverStartWatchingSection
import com.thomaskioko.tvmaniac.discover.ui.section.DiscoverUpNextSection
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_empty_content
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_error_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.missing_api_key
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags
import io.github.thomaskioko.codegen.annotations.TabUi

@TabUi(presenter = DiscoverShowsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun DiscoverScreen(
    presenter: DiscoverShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val hostState by presenter.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val dismissSnackbarState = rememberDismissState { value ->
        if (value != DismissValue.Default) {
            snackBarHostState.currentSnackbarData?.dismiss()
            true
        } else {
            false
        }
    }

    DiscoverScaffold(
        modifier = modifier,
        hostState = hostState,
        snackBarHostState = snackBarHostState,
        dismissSnackbarState = dismissSnackbarState,
        onHostAction = presenter::dispatch,
    ) {
        DiscoverLazyColumn(
            onSearch = { presenter.dispatch(SearchIconClicked) },
            onRefresh = { presenter.dispatch(RefreshData) },
            isRefreshing = hostState.isRefreshing,
        ) {
            item(key = DiscoverTestTags.FEATURED_PAGER_TEST_TAG) {
                DiscoverFeaturedSection(presenter = presenter.featuredPresenter)
            }
            item(key = DiscoverTestTags.UP_NEXT_SECTION_TEST_TAG) {
                DiscoverUpNextSection(presenter = presenter.upNextPresenter)
            }
            item(key = DiscoverTestTags.ROW_KEY_START_WATCHING) {
                DiscoverStartWatchingSection(presenter = presenter.startWatchingPresenter)
            }
            item(key = DiscoverTestTags.CATALOG_SECTION_TEST_TAG) {
                DiscoverCatalogSection(presenter = presenter.catalogPresenter)
            }
        }
    }

    TvManiacSnackBarHost(
        message = hostState.message?.message,
        style = SnackBarStyle.Error,
        onDismiss = { hostState.message?.let { presenter.dispatch(MessageShown(it.id)) } },
    )
}

@Composable
internal fun DiscoverScaffold(
    hostState: DiscoverViewState,
    snackBarHostState: SnackbarHostState,
    dismissSnackbarState: DismissState,
    onHostAction: (DiscoverShowAction) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier.testTag(DiscoverTestTags.SCREEN_TEST_TAG),
    ) { paddingValues ->
        when {
            hostState.isLoading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(DiscoverTestTags.PROGRESS_INDICATOR)
                    .padding(paddingValues.copy(copyBottom = false)),
            )

            hostState.showError -> EmptyStateView(
                imageVector = Icons.Outlined.ErrorOutline,
                title = hostState.message?.message ?: generic_error_message.resolve(context),
                buttonText = generic_retry.resolve(context),
                buttonTestTag = DiscoverTestTags.ERROR_RETRY_BUTTON_TEST_TAG,
                onClick = { onHostAction(RefreshData) },
            )

            hostState.isEmpty -> EmptyStateView(
                modifier = Modifier
                    .padding(paddingValues.copy(copyBottom = false)),
                imageVector = Icons.Filled.Movie,
                title = generic_empty_content.resolve(context),
                message = missing_api_key.resolve(context),
                buttonText = generic_retry.resolve(context),
                buttonTestTag = DiscoverTestTags.ERROR_RETRY_BUTTON_TEST_TAG,
                onClick = { onHostAction(RefreshData) },
            )

            else -> content()
        }
    }
}

@Composable
internal fun DiscoverLazyColumn(
    isRefreshing: Boolean,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(refreshing = false, onRefresh = onRefresh)
    val listState = rememberLazyListState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag(DiscoverTestTags.DISCOVER_LIST_TEST_TAG)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
            state = listState,
        ) {
            content()

            item(key = "spacer_bottom") {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
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
                    text = label_discover_title.resolve(context),
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
            actions = { showAppBarBackground ->
                ScrimButton(
                    show = showAppBarBackground,
                    onClick = onSearch,
                    modifier = Modifier
                        .padding(end = TvManiacSpacing.xSmall)
                        .testTag(DiscoverTestTags.SEARCH_BUTTON_TEST_TAG),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = cd_search.resolve(context),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DiscoverScreenLoadingPreview() {
    DiscoverScaffold(
        hostState = DiscoverViewState(isLoading = true),
        snackBarHostState = remember { SnackbarHostState() },
        dismissSnackbarState = rememberDismissState { true },
        onHostAction = {},
        content = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DiscoverScreenEmptyPreview() {
    DiscoverScaffold(
        hostState = DiscoverViewState(isEmpty = true),
        snackBarHostState = remember { SnackbarHostState() },
        dismissSnackbarState = rememberDismissState { true },
        onHostAction = {},
        content = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DiscoverScreenErrorPreview() {
    DiscoverScaffold(
        hostState = DiscoverViewState(
            showError = true,
            message = com.thomaskioko.tvmaniac.core.view.UiMessage("Opps! Something went wrong"),
        ),
        snackBarHostState = remember { SnackbarHostState() },
        dismissSnackbarState = rememberDismissState { true },
        onHostAction = {},
        content = {},
    )
}
