package com.thomaskioko.tvmaniac.ui.library

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_watchlist_empty_result
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_library
import com.thomaskioko.tvmaniac.i18n.MR.strings.msg_search_show_hint
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presentation.library.ChangeListStyleClicked
import com.thomaskioko.tvmaniac.presentation.library.ChangeSortOption
import com.thomaskioko.tvmaniac.presentation.library.ClearFilters
import com.thomaskioko.tvmaniac.presentation.library.ClearLibraryQuery
import com.thomaskioko.tvmaniac.presentation.library.LibraryAction
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.library.LibraryQueryChanged
import com.thomaskioko.tvmaniac.presentation.library.LibraryShowClicked
import com.thomaskioko.tvmaniac.presentation.library.LibraryState
import com.thomaskioko.tvmaniac.presentation.library.ToggleGenreFilter
import com.thomaskioko.tvmaniac.presentation.library.ToggleSearchActive
import com.thomaskioko.tvmaniac.presentation.library.ToggleStatusFilter
import com.thomaskioko.tvmaniac.presentation.library.model.LibraryShowItem
import com.thomaskioko.tvmaniac.ui.library.preview.LibraryStatePreviewParameterProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
public fun LibraryScreen(
    presenter: LibraryPresenter,
    modifier: Modifier = Modifier,
) {
    val libraryState by presenter.state.collectAsState()

    LibraryScreen(
        modifier = modifier,
        state = libraryState,
        onAction = presenter::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryScreen(
    state: LibraryState,
    modifier: Modifier = Modifier,
    onAction: (LibraryAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showSortOptions by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        topBar = {
            TopBar(
                onAction = onAction,
                state = state,
                scrollBehavior = scrollBehavior,
                onFilterClick = { showSortOptions = true },
            )
        },
        content = { contentPadding ->
            val context = LocalContext.current
            AnimatedContent(
                modifier = Modifier
                    .padding(contentPadding.copy(copyBottom = false))
                    .padding(horizontal = 8.dp),
                targetState = state.isGridMode,
                transitionSpec = {
                    (scaleIn(animationSpec = spring()) + fadeIn()) togetherWith
                        (scaleOut(animationSpec = spring()) + fadeOut())
                },
                label = "list_style_animation",
            ) { isGridMode ->
                when {
                    state.showLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            LoadingIndicator()
                        }
                    }
                    state.isEmpty -> {
                        val message = if (state.query.isNotBlank()) {
                            label_watchlist_empty_result.resolve(context).format(state.query)
                        } else {
                            null
                        }
                        EmptyContent(
                            imageVector = Icons.Outlined.Inbox,
                            message = message,
                        )
                    }
                    isGridMode -> {
                        LibraryGridContent(
                            items = state.items,
                            scrollBehavior = scrollBehavior,
                            onItemClicked = { onAction(LibraryShowClicked(it)) },
                        )
                    }
                    else -> {
                        LibraryListContent(
                            items = state.items,
                            scrollBehavior = scrollBehavior,
                            onItemClicked = { onAction(LibraryShowClicked(it)) },
                        )
                    }
                }
            }
        },
    )

    if (showSortOptions) {
        ModalBottomSheet(
            onDismissRequest = { showSortOptions = false },
            sheetState = sheetState,
        ) {
            SortOptionsContent(
                state = state,
                onSortOptionSelected = { sortOption ->
                    onAction(ChangeSortOption(sortOption))
                },
                onGenreToggle = { genre ->
                    onAction(ToggleGenreFilter(genre))
                },
                onStatusToggle = { status ->
                    onAction(ToggleStatusFilter(status))
                },
                onClearFilters = {
                    onAction(ClearFilters)
                },
                onApplyFilters = {
                    scope.launch {
                        sheetState.hide()
                        showSortOptions = false
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onAction: (LibraryAction) -> Unit,
    state: LibraryState,
    scrollBehavior: TopAppBarScrollBehavior,
    onFilterClick: () -> Unit,
) {
    val context = LocalContext.current

    BackHandler(enabled = state.isSearchActive) {
        onAction(ClearLibraryQuery)
        onAction(ToggleSearchActive)
    }

    TvManiacTopBar(
        title = {
            AnimatedContent(
                targetState = state.isSearchActive,
                transitionSpec = {
                    (scaleIn(animationSpec = spring()) + fadeIn()) togetherWith
                        (scaleOut(animationSpec = spring()) + fadeOut())
                },
                label = "search_expansion_animation",
            ) { expanded ->
                if (expanded) {
                    LibrarySearchbar(
                        query = state.query,
                        hint = msg_search_show_hint.resolve(context),
                        onQueryChanged = { onAction(LibraryQueryChanged(it)) },
                        onCloseClick = {
                            onAction(ClearLibraryQuery)
                            onAction(ToggleSearchActive)
                        },
                    )
                } else {
                    CollapsedTopBarContent(
                        state = state,
                        onAction = onAction,
                        onSearchClick = { onAction(ToggleSearchActive) },
                        onFilterClick = onFilterClick,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun CollapsedTopBarContent(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.padding(end = 8.dp),
            onClick = { onAction(ChangeListStyleClicked(state.isGridMode)) },
        ) {
            val image = if (state.isGridMode) {
                Icons.AutoMirrored.Outlined.List
            } else {
                Icons.Outlined.GridView
            }
            Icon(
                imageVector = image,
                contentDescription = "Toggle list style",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = menu_item_library.resolve(context),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = cd_search.resolve(context),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            AnimatedVisibility(visible = state.isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LibraryGridContent(
    items: ImmutableList<LibraryShowItem>,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClicked: (Long) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(horizontal = 4.dp),
    ) {
        items(items, key = { it.traktId }) { item ->
            LibraryGridItem(
                item = item,
                onItemClicked = onItemClicked,
            )
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun LibraryGridItem(
    item: LibraryShowItem,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    PosterCard(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f),
        imageUrl = item.posterImageUrl,
        title = item.title,
        onClick = { onItemClicked(item.traktId) },
        shape = RectangleShape,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryListContent(
    items: ImmutableList<LibraryShowItem>,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClicked: (Long) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        items(items.size, key = { items[it].traktId }) { index ->
            LibraryListItem(
                item = items[index],
                onItemClicked = onItemClicked,
            )
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Preview
@Composable
private fun LibraryScreenPreview(
    @PreviewParameter(LibraryStatePreviewParameterProvider::class) state: LibraryState,
) {
    TvManiacTheme {
        Surface {
            LibraryScreen(
                state = state,
                onAction = {},
            )
        }
    }
}
