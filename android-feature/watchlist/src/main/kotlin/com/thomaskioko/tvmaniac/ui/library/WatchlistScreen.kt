package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.watchlist.EmptyWatchlist
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistAction
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistContent
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistShowClicked
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistState
import com.thomaskioko.tvmaniac.presentation.watchlist.LoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.ReloadWatchlist
import com.thomaskioko.tvmaniac.presentation.watchlist.model.WatchlistItem
import com.thomaskioko.tvmaniac.resources.R
import kotlinx.collections.immutable.ImmutableList

@Composable
fun WatchlistScreen(
    presenter: WatchlistPresenter,
    modifier: Modifier = Modifier,
) {
  val libraryState by presenter.state.collectAsState()

  WatchlistScreen(
    modifier = modifier,
    state = libraryState,
    onAction = presenter::dispatch,
  )
}

@Composable
internal fun WatchlistScreen(
  state: WatchlistState,
  modifier: Modifier = Modifier,
  onAction: (WatchlistAction) -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

  Scaffold(
    modifier = modifier.statusBarsPadding(),
    topBar = {
      TvManiacTopBar(
        title = {
          Text(
            text = stringResource(id = R.string.menu_item_library),
            style =
              MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
              ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
          )
        },
        scrollBehavior = scrollBehavior,
        colors =
          TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
          ),
      )
    },
    content = { contentPadding ->
      when (state) {
        is LoadingShows ->
          LoadingIndicator(
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
          )
        is EmptyWatchlist ->
          ErrorUi(
            errorIcon = {
              Image(
                modifier = Modifier.size(120.dp),
                imageVector = Icons.Outlined.ErrorOutline,
                colorFilter =
                  ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F)),
                contentDescription = null,
              )
            },
            onRetry = { onAction(ReloadWatchlist) },
            errorMessage = state.message,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
          )
        is WatchlistContent -> {
          when {
            state.list.isEmpty() ->
              EmptyContent(
                imageVector = Icons.Outlined.Inbox,
                message = stringResource(id = R.string.error_empty_library),
              )
            else ->
              WatchlistGridContent(
                list = state.list,
                scrollBehavior = scrollBehavior,
                paddingValues = contentPadding,
                onItemClicked = { onAction(WatchlistShowClicked(it)) },
              )
          }
        }
      }
    },
  )
}

@Composable
private fun WatchlistGridContent(
  list: ImmutableList<WatchlistItem>,
  scrollBehavior: TopAppBarScrollBehavior,
  paddingValues: PaddingValues,
  onItemClicked: (Long) -> Unit,
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    modifier =
      Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        .padding(horizontal = 4.dp)
        .padding(paddingValues.copy(copyBottom = false)),
  ) {
    items(list) { show ->
      PosterCard(
        modifier = Modifier.animateItem(),
        imageUrl = show.posterImageUrl,
        title = show.title,
        onClick = { onItemClicked(show.tmdbId) },
      )
    }
  }
}

@ThemePreviews
@Composable
private fun WatchlistScreenPreview(
  @PreviewParameter(WatchlistPreviewParameterProvider::class) state: WatchlistState,
) {
  TvManiacTheme {
    Surface {
      WatchlistScreen(
        state = state,
        onAction = {},
      )
    }
  }
}
