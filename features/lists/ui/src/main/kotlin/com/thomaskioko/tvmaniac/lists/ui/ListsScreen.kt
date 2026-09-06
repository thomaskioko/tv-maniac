package com.thomaskioko.tvmaniac.lists.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.ListCollageCard
import com.thomaskioko.tvmaniac.compose.components.ListCollageCardDefaults
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.lists.presenter.ListsAction
import com.thomaskioko.tvmaniac.lists.presenter.ListsPresenter
import com.thomaskioko.tvmaniac.lists.presenter.ListsState
import com.thomaskioko.tvmaniac.testtags.lists.ListsTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi

private const val LISTS_GRID_COLUMNS = 2
private const val LOADING_PLACEHOLDER_COUNT = 4

@ScreenUi(presenter = ListsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun ListsScreen(
    presenter: ListsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    ListsScreen(
        state = state,
        onAction = presenter::dispatch,
        modifier = modifier,
    )
}

@Composable
internal fun ListsScreen(
    state: ListsState,
    onAction: (ListsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.testTag(ListsTestTags.SCREEN_TEST_TAG),
        topBar = {
            TvManiacTopBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = { onAction(ListsAction.BackClicked) })
                            .padding(TvManiacSpacing.medium)
                            .testTag(ListsTestTags.BACK_BUTTON_TEST_TAG),
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
        ListsBody(
            state = state,
            contentPadding = contentPadding,
            onAction = onAction,
        )
    }
}

@Composable
private fun ListsBody(
    state: ListsState,
    contentPadding: PaddingValues,
    onAction: (ListsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorMessage = state.errorMessage
    when {
        state.isLoading -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(LISTS_GRID_COLUMNS),
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentPadding = PaddingValues(TvManiacSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.medium),
            ) {
                items(LOADING_PLACEHOLDER_COUNT) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ListCollageCardDefaults.Height),
                        shape = MaterialTheme.shapes.large,
                    )
                }
            }
        }

        errorMessage != null -> {
            EmptyStateView(
                title = errorMessage,
                imageVector = Icons.Outlined.Warning,
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }

        state.lists.isEmpty() -> {
            EmptyStateView(
                title = state.emptyMessage,
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(LISTS_GRID_COLUMNS),
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentPadding = PaddingValues(TvManiacSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.medium),
            ) {
                items(
                    items = state.lists,
                    key = { it.id },
                ) { list ->
                    ListCollageCard(
                        name = list.name,
                        itemCountLabel = list.itemCountLabel,
                        posterUrls = list.posterUrls,
                        onClick = { onAction(ListsAction.ListClicked(list.id)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(ListsTestTags.listCard(list.id)),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ListsScreenPreview(
    @PreviewParameter(ListsPreviewParameterProvider::class) state: ListsState,
) {
    ListsScreen(
        state = state,
        onAction = {},
    )
}
