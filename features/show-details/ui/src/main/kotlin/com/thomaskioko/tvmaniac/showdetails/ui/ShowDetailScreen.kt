package com.thomaskioko.tvmaniac.showdetails.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.actionIconWhen
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_navigate_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_error_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsBackClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsMessageShown
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsReload
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsCastSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsHeaderSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsProvidersSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsSeasonEpisodesSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsSimilarSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsTrailersSection
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = ShowDetailsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun ShowDetailsScreen(
    presenter: ShowDetailsPresenter,
    modifier: Modifier = Modifier,
) {
    val hostState by presenter.state.collectAsState()
    val headerState by presenter.headerPresenter.state.collectAsState()
    val listState = rememberLazyListState()

    ShowDetailsScaffold(
        hostState = hostState,
        title = headerState.title,
        isHeaderEmpty = headerState.tmdbId == 0L,
        listState = listState,
        onHostAction = presenter::dispatch,
        modifier = modifier,
    ) {
        item(key = "header") {
            ShowDetailsHeaderSection(presenter = presenter.headerPresenter)
        }
        item(key = "season_episodes") {
            ShowDetailsSeasonEpisodesSection(
                presenter = presenter.seasonsEpisodesPresenter,
                status = headerState.status,
            )
        }
        item(key = "providers") {
            ShowDetailsProvidersSection(presenter = presenter.providersPresenter)
        }
        item(key = "trailers") {
            ShowDetailsTrailersSection(presenter = presenter.trailersPresenter)
        }
        item(key = "casts") {
            ShowDetailsCastSection(presenter = presenter.castPresenter)
        }
        item(key = "similar") {
            ShowDetailsSimilarSection(presenter = presenter.similarPresenter)
        }
        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(54.dp))
        }
    }
}

@Composable
internal fun ShowDetailsScaffold(
    hostState: ShowDetailsState,
    title: String,
    isHeaderEmpty: Boolean,
    listState: LazyListState,
    onHostAction: (ShowDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .testTag(ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG)
                    .fillMaxSize(),
                state = listState,
                contentPadding = contentPadding.copy(copyTop = false),
            ) {
                if (!hostState.isRefreshing && isHeaderEmpty && hostState.message != null) {
                    item(key = "error") {
                        EmptyStateView(
                            modifier = Modifier.padding(top = 16.dp),
                            imageVector = Icons.Outlined.ErrorOutline,
                            title = generic_error_message.resolve(LocalContext.current),
                            buttonText = generic_retry.resolve(LocalContext.current),
                            buttonTestTag = ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG,
                            onClick = { onHostAction(ShowDetailsReload) },
                        )
                    }
                } else {
                    content()
                }
            }

            RefreshCollapsableTopAppBar(
                listState = listState,
                isRefreshing = hostState.isRefreshing,
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = cd_navigate_back.resolve(LocalContext.current),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navIconModifier = Modifier.testTag(ShowDetailsTestTags.BACK_BUTTON_TEST_TAG),
                actionIcon = actionIconWhen(hostState.message == null) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.testTag(ShowDetailsTestTags.REFRESH_BUTTON_TEST_TAG),
                    )
                },
                onNavIconClicked = { onHostAction(ShowDetailsBackClicked) },
                onActionIconClicked = { onHostAction(ShowDetailsReload) },
            )

            TvManiacSnackBarHost(
                message = hostState.message?.message,
                style = SnackBarStyle.Error,
                onDismiss = { hostState.message?.let { onHostAction(ShowDetailsMessageShown(it.id)) } },
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsScaffoldEmptyPreview() {
    ShowDetailsScaffold(
        hostState = previewHostState,
        title = "",
        isHeaderEmpty = true,
        listState = LazyListState(),
        onHostAction = {},
        content = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsScaffoldErrorPreview() {
    ShowDetailsScaffold(
        hostState = previewHostStateWithMessage,
        title = "",
        isHeaderEmpty = true,
        listState = LazyListState(),
        onHostAction = {},
        content = {},
    )
}
