package com.thomaskioko.tvmaniac.watchlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LazyGridItems
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.navigation.extensions.viewModel
import com.thomaskioko.tvmaniac.presentation.watchlist.ErrorLoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.LoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.ReloadWatchlist
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistContent
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistItem
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistState
import com.thomaskioko.tvmaniac.resources.R
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias WatchList = @Composable (
    onShowClicked: (showId: Long) -> Unit,
) -> Unit

@Inject
@Composable
fun WatchList(
    viewModelFactory: () -> WatchlistViewModel,
    @Assisted onShowClicked: (showId: Long) -> Unit,
) {
    WatchlistScreen(
        viewModel = viewModel(factory = viewModelFactory),
        onShowClicked = onShowClicked,
    )
}

@Composable
internal fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onShowClicked: (showId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val followedState by viewModel.state.collectAsStateWithLifecycle()

    WatchlistScreen(
        modifier = modifier,
        state = followedState,
        onShowClicked = onShowClicked,
        onRetry = { viewModel.dispatch(ReloadWatchlist) },
    )
}

@Composable
private fun WatchlistScreen(
    state: WatchlistState,
    onShowClicked: (showId: Long) -> Unit,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier
            .statusBarsPadding(),
        content = { contentPadding ->

            when (state) {
                is LoadingShows ->
                    LoadingIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                    )

                is ErrorLoadingShows ->
                    ErrorUi(
                        onRetry = onRetry,
                        errorMessage = state.message,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                    )

                is WatchlistContent -> {
                    when {
                        state.list.isEmpty() -> EmptyContent(
                            painter = painterResource(id = R.drawable.ic_watchlist_empty),
                            message = stringResource(id = R.string.msg_empty_favorites),
                        )

                        else -> FollowingGridContent(
                            list = state.list,
                            paddingValues = contentPadding,
                            onItemClicked = onShowClicked,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun FollowingGridContent(
    list: List<WatchlistItem>,
    paddingValues: PaddingValues,
    onItemClicked: (Long) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyGridItems(
        listState = listState,
        items = list,
        paddingValues = paddingValues,
    ) { show ->

        TvPosterCard(
            posterImageUrl = show.posterImageUrl,
            title = show.title,
            onClick = { onItemClicked(show.traktId) },
        )
    }
}

@ThemePreviews
@Composable
private fun FollowingScreenPreview(
    @PreviewParameter(FollowingPreviewParameterProvider::class)
    state: WatchlistState,
) {
    TvManiacTheme {
        Surface {
            WatchlistScreen(
                state = state,
                onShowClicked = {},
            )
        }
    }
}
