package com.thomaskioko.tvmaniac.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterBackdropCard
import com.thomaskioko.tvmaniac.compose.components.SearchTextContainer
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_empty_content
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.missing_api_key
import com.thomaskioko.tvmaniac.i18n.MR.strings.msg_search_show_hint
import com.thomaskioko.tvmaniac.i18n.MR.strings.search_no_results
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.search.presenter.ClearQuery
import com.thomaskioko.tvmaniac.search.presenter.DismissSnackBar
import com.thomaskioko.tvmaniac.search.presenter.EmptySearchResult
import com.thomaskioko.tvmaniac.search.presenter.GenreCategoryClicked
import com.thomaskioko.tvmaniac.search.presenter.InitialSearchState
import com.thomaskioko.tvmaniac.search.presenter.QueryChanged
import com.thomaskioko.tvmaniac.search.presenter.ReloadShowContent
import com.thomaskioko.tvmaniac.search.presenter.SearchResultAvailable
import com.thomaskioko.tvmaniac.search.presenter.SearchShowAction
import com.thomaskioko.tvmaniac.search.presenter.SearchShowClicked
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.search.presenter.ShowContentAvailable
import com.thomaskioko.tvmaniac.search.presenter.model.ShowGenre
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import com.thomaskioko.tvmaniac.search.ui.components.SearchResultItem
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

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

@Composable
internal fun SearchScreen(
    state: SearchShowState,
    modifier: Modifier = Modifier,
    onAction: (SearchShowAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TvManiacTopBar(
                title = {
                    Text(
                        text = menu_item_search.resolve(LocalContext.current),
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
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                snackBarHostState = snackBarHostState,
                lazyListState = lazyListState,
            )
        },
    )
}

@Composable
private fun SearchScreenContent(
    state: SearchShowState,
    paddingValues: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    onAction: (SearchShowAction) -> Unit,
    snackBarHostState: SnackbarHostState,
    lazyListState: LazyListState,
) {
    SearchScreenHeader(
        query = state.query ?: "",
        paddingValues = paddingValues,
        scrollBehavior = scrollBehavior,
        onAction = onAction,
        lazyListState = lazyListState,
    ) {
        when (state) {
            is EmptySearchResult -> {
                val context = LocalContext.current
                if (state.errorMessage != null) {
                    EmptyContent(
                        imageVector = Icons.Outlined.ErrorOutline,
                        title = generic_empty_content.resolve(context),
                        message = missing_api_key.resolve(context),
                        buttonText = generic_retry.resolve(context),
                        onClick = { onAction(ReloadShowContent) },
                    )
                } else {
                    EmptyContent(
                        imageVector = Icons.Filled.SearchOff,
                        title = search_no_results.resolve(LocalContext.current),
                    )
                }
            }

            is SearchResultAvailable -> SearchResultsContent(
                onAction = onAction,
                results = state.results,
                scrollState = lazyListState,
            )

            is ShowContentAvailable -> ShowContent(
                onAction = onAction,
                genres = state.genres,
                errorMessage = state.errorMessage,
                snackBarHostState = snackBarHostState,
                lazyListState = lazyListState,
            )

            is InitialSearchState -> LoadingIndicator()
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
            .padding(horizontal = 16.dp)
            .padding(paddingValues.copy(copyBottom = false)),
    ) {
        SearchTextContainer(
            query = query,
            hint = msg_search_show_hint.resolve(LocalContext.current),
            lazyListState = lazyListState,
            content = content,
            onClearQuery = { onAction(ClearQuery) },
            onQueryChanged = { onAction(QueryChanged(it)) },
        )
    }
}

@Composable
private fun SearchResultsContent(
    onAction: (SearchShowAction) -> Unit,
    scrollState: LazyListState,
    results: ImmutableList<ShowItem>?,
    modifier: Modifier = Modifier,
) {
    if (results.isNullOrEmpty()) return

    LazyColumn(
        modifier = modifier,
        state = scrollState,
    ) {
        items(results) { item ->

            Spacer(modifier = Modifier.height(8.dp))

            SearchResultItem(
                title = item.title,
                status = item.status,
                voteAverage = item.voteAverage,
                year = item.year,
                overview = item.overview,
                imageUrl = item.posterImageUrl,
                onClick = { onAction(SearchShowClicked(item.tmdbId)) },
            )
        }
    }
}

@Composable
private fun ShowContent(
    errorMessage: String?,
    snackBarHostState: SnackbarHostState,
    lazyListState: LazyListState,
    onAction: (SearchShowAction) -> Unit,
    genres: ImmutableList<ShowGenre>,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            val snackBarResult = snackBarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short,
            )

            when (snackBarResult) {
                SnackbarResult.ActionPerformed,
                SnackbarResult.Dismissed,
                -> onAction(DismissSnackBar)
            }
        }
    }

    GenreContent(
        genres = genres,
        onItemClicked = { onAction(GenreCategoryClicked(it)) },
        modifier = modifier,
        lazyListState = lazyListState,
    )
}

@Composable
private fun GenreContent(
    lazyListState: LazyListState,
    genres: ImmutableList<ShowGenre>,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 4.dp),
    ) {
        if (genres.isEmpty()) return

        BoxTextItems(
            modifier = Modifier.padding(vertical = 12.dp),
            title = "Browse by Genre",
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(genres) { showGenre ->

                PosterBackdropCard(
                    textAlign = TextAlign.Center,
                    imageUrl = showGenre.posterUrl,
                    title = showGenre.name,
                    modifier = Modifier
                        .width(160.dp)
                        .heightIn(160.dp, 220.dp),
                    onClick = { onItemClicked(showGenre.id) },
                )
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
