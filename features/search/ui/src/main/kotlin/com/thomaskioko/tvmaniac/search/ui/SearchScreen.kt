package com.thomaskioko.tvmaniac.search.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.FilterChipSection
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.SearchTextContainer
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_empty_content
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.missing_api_key
import com.thomaskioko.tvmaniac.i18n.MR.strings.msg_search_show_hint
import com.thomaskioko.tvmaniac.i18n.MR.strings.search_no_results
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.search.presenter.BackClicked
import com.thomaskioko.tvmaniac.search.presenter.CategoryChanged
import com.thomaskioko.tvmaniac.search.presenter.ClearQuery
import com.thomaskioko.tvmaniac.search.presenter.MessageShown
import com.thomaskioko.tvmaniac.search.presenter.QueryChanged
import com.thomaskioko.tvmaniac.search.presenter.ReloadShowContent
import com.thomaskioko.tvmaniac.search.presenter.SearchShowAction
import com.thomaskioko.tvmaniac.search.presenter.SearchShowClicked
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.BrowsingGenres
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.Error
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.InitialLoading
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchEmpty
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchLoading
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchResults
import com.thomaskioko.tvmaniac.search.presenter.model.GenreRowModel
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import com.thomaskioko.tvmaniac.search.ui.components.HorizontalShowContentRow
import com.thomaskioko.tvmaniac.search.ui.components.SearchResultItem
import com.thomaskioko.tvmaniac.testtags.search.SearchTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableSet

@ScreenUi(presenter = SearchShowsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun SearchScreen(
    presenter: SearchShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    SearchScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    state: SearchShowState,
    modifier: Modifier = Modifier,
    onAction: (SearchShowAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val isBrowsingGenres = state.uiState is BrowsingGenres

    LaunchedEffect(state.message, state.uiState) {
        if (state.uiState is Error) return@LaunchedEffect
        state.message?.let { message ->
            val snackBarResult = snackBarHostState.showSnackbar(
                message = message.message,
                duration = SnackbarDuration.Short,
            )

            when (snackBarResult) {
                SnackbarResult.ActionPerformed,
                SnackbarResult.Dismissed,
                -> onAction(MessageShown(message.id))
            }
        }
    }

    Scaffold(
        modifier = modifier
            .statusBarsPadding()
            .testTag(SearchTestTags.SCREEN_TEST_TAG),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TvManiacTopBar(
                title = {
                    Text(
                        text = menu_item_search.resolve(context),
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
                navigationIcon = {
                    IconButton(onClick = { onAction(BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = cd_back.resolve(context),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                actions = {
                    if (isBrowsingGenres) {
                        AnimatedVisibility(visible = state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                strokeWidth = 2.dp,
                            )
                        }
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                imageVector = Icons.Outlined.FilterList,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
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
        },
        content = { paddingValues ->
            SearchScreenContent(
                state = state,
                paddingValues = paddingValues,
                scrollBehavior = scrollBehavior,
                onAction = onAction,
                lazyListState = lazyListState,
            )
        },
    )

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
        ) {
            FilterChipSection(
                modifier = Modifier.padding(16.dp),
                title = state.categoryTitle,
                items = state.categories,
                selectedItems = state.categories
                    .filter { it.category == state.selectedCategory }
                    .toImmutableSet(),
                onItemToggle = { item ->
                    onAction(CategoryChanged(item.category))
                    showFilterSheet = false
                },
                labelProvider = { it.label },
                collapsedItemCount = state.categories.size,
                singleSelect = true,
            )
        }
    }
}

@Composable
private fun SearchScreenContent(
    state: SearchShowState,
    paddingValues: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    onAction: (SearchShowAction) -> Unit,
    lazyListState: LazyListState,
) {
    SearchScreenHeader(
        query = state.query,
        paddingValues = paddingValues,
        scrollBehavior = scrollBehavior,
        onAction = onAction,
        lazyListState = lazyListState,
    ) {
        when (val uiState = state.uiState) {
            InitialLoading, SearchLoading -> LoadingIndicator()
            SearchEmpty -> {
                EmptyStateView(
                    modifier = Modifier.testTag(SearchTestTags.EMPTY_STATE_TEST_TAG),
                    imageVector = Icons.Filled.SearchOff,
                    title = search_no_results.resolve(LocalContext.current),
                )
            }

            is SearchResults -> SearchResultsContent(
                modifier = Modifier.padding(horizontal = 16.dp),
                onAction = onAction,
                results = uiState.results,
                scrollState = lazyListState,
                isUpdating = uiState.isUpdating,
            )

            is BrowsingGenres -> GenreRowsContent(
                genreRows = uiState.genreRows,
                onShowClicked = { onAction(SearchShowClicked(it)) },
            )

            is Error -> {
                val context = LocalContext.current
                EmptyStateView(
                    modifier = Modifier.testTag(SearchTestTags.ERROR_STATE_TEST_TAG),
                    imageVector = Icons.Outlined.ErrorOutline,
                    title = generic_empty_content.resolve(context),
                    message = state.message?.message ?: missing_api_key.resolve(context),
                    buttonText = generic_retry.resolve(context),
                    onClick = { onAction(ReloadShowContent) },
                )
            }
        }
    }
}

@Composable
private fun SearchScreenHeader(
    query: String,
    onAction: (SearchShowAction) -> Unit,
    paddingValues: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(paddingValues.copy(copyBottom = false)),
    ) {
        SearchTextContainer(
            query = query,
            hint = msg_search_show_hint.resolve(LocalContext.current),
            lazyListState = lazyListState,
            content = content,
            textFieldModifier = Modifier.testTag(SearchTestTags.SEARCH_BAR_TEST_TAG),
            onClearQuery = { onAction(ClearQuery) },
            onQueryChanged = { onAction(QueryChanged(it)) },
        )
    }
}

@Composable
private fun SearchResultsContent(
    onAction: (SearchShowAction) -> Unit,
    scrollState: LazyListState,
    results: ImmutableList<ShowItem>,
    isUpdating: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (isUpdating) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        LazyColumn(
            state = scrollState,
        ) {
            items(
                items = results,
                key = { it.traktId },
                contentType = { "SearchResult" },
            ) { item ->

                Spacer(modifier = Modifier.height(8.dp))

                SearchResultItem(
                    modifier = Modifier.testTag(SearchTestTags.resultItem(item.traktId)),
                    title = item.title,
                    status = item.status,
                    voteAverage = item.voteAverage,
                    year = item.year,
                    overview = item.overview,
                    imageUrl = item.posterImageUrl,
                    onClick = { onAction(SearchShowClicked(item.traktId)) },
                )
            }
        }
    }
}

@Composable
private fun GenreRowsContent(
    genreRows: ImmutableList<GenreRowModel>,
    onShowClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 32.dp),
    ) {
        if (genreRows.isEmpty()) return

        LazyColumn {
            items(
                items = genreRows,
                key = { it.slug },
                contentType = { "GenreRow" },
            ) { genreRow ->
                HorizontalShowContentRow(
                    title = genreRow.name,
                    description = genreRow.subtitle,
                    tvShows = genreRow.shows,
                    onItemClicked = onShowClicked,
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SearchContentPreview(
    @PreviewParameter(SearchPreviewParameterProvider::class) state: SearchShowState,
) {
    TvManiacTheme {
        Surface {
            SearchScreen(
                state = state,
                onAction = {},
            )
        }
    }
}
