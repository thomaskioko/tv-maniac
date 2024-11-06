package com.thomaskioko.tvmaniac.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.search.DismissSnackBar
import com.thomaskioko.tvmaniac.presentation.search.EmptySearchState
import com.thomaskioko.tvmaniac.presentation.search.ErrorSearchState
import com.thomaskioko.tvmaniac.presentation.search.ReloadShowContent
import com.thomaskioko.tvmaniac.presentation.search.SearchResultAvailable
import com.thomaskioko.tvmaniac.presentation.search.SearchShowAction
import com.thomaskioko.tvmaniac.presentation.search.SearchShowClicked
import com.thomaskioko.tvmaniac.presentation.search.SearchShowState
import com.thomaskioko.tvmaniac.presentation.search.SearchShowsComponent
import com.thomaskioko.tvmaniac.presentation.search.ShowContentAvailable
import com.thomaskioko.tvmaniac.presentation.search.ShowItem
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.ui.search.components.HorizontalShowContentRow
import com.thomaskioko.tvmaniac.ui.search.components.SearchResultItem
import com.thomaskioko.tvmaniac.ui.search.components.SearchTextContainer
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SearchScreen(
  component: SearchShowsComponent,
  modifier: Modifier = Modifier,
) {
  val state by component.state.collectAsState()

  SearchScreen(
    modifier = modifier,
    state = state,
    onAction = component::dispatch,
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
            text = stringResource(id = R.string.menu_item_search),
            style =
            MaterialTheme.typography.titleLarge.copy(
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
        colors =
        TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
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
      is EmptySearchState -> EmptyContent(
        imageVector = Icons.Filled.SearchOff,
        title = stringResource(R.string.search_no_results),
      )
      is ErrorSearchState -> EmptyContent(
        imageVector = Icons.Outlined.ErrorOutline,
        title =  stringResource(R.string.generic_empty_content),
        message = stringResource(R.string.missing_api_key),
        buttonText = stringResource(id = R.string.generic_retry),
        onClick = { onAction(ReloadShowContent) },
      )
      is SearchResultAvailable ->
        SearchResultsContent(
          onAction = onAction,
          results = state.results,
          scrollState = lazyListState,
        )
      is ShowContentAvailable -> {
        ShowContent(
          onAction = onAction,
          featuredShows = state.featuredShows,
          trendingShows = state.trendingShows,
          upcomingShows = state.upcomingShows,
          errorMessage = state.errorMessage,
          snackBarHostState = snackBarHostState,
          lazyListState = lazyListState,
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
      .padding(horizontal = 16.dp)
      .padding(paddingValues.copy(copyBottom = false)),
  ) {
    SearchTextContainer(
      query = query,
      hint = stringResource(id = R.string.msg_search_show_hint),
      lazyListState = lazyListState,
      onAction = onAction,
      content = content,
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
  featuredShows: ImmutableList<ShowItem>?,
  trendingShows: ImmutableList<ShowItem>?,
  upcomingShows: ImmutableList<ShowItem>?,
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

  LazyColumn(
    modifier =
    modifier
      .fillMaxSize(),
    state = lazyListState,
  ) {
    item {
      Spacer(Modifier.height(16.dp))
    }

    item {
      HorizontalShowContentRow(
        title = stringResource(id = R.string.title_category_featured),
        tvShows = featuredShows,
        onItemClicked = { onAction(SearchShowClicked(it)) },
      )
    }

    item {
      Spacer(Modifier.height(8.dp))
    }

    item {
      HorizontalShowContentRow(
        title = stringResource(id = R.string.title_category_trending_today),
        tvShows = trendingShows,
        onItemClicked = { onAction(SearchShowClicked(it)) },
      )
    }

    item {
      Spacer(Modifier.height(8.dp))
    }

    item {
      HorizontalShowContentRow(
        title = stringResource(id = R.string.title_category_upcoming),
        tvShows = upcomingShows,
        onItemClicked = { onAction(SearchShowClicked(it)) },
      )
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
