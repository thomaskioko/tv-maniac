package com.thomaskioko.tvmaniac.watchlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import cafe.adriel.voyager.core.screen.Screen
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LazyGridItems
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.watchlist.ErrorLoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryContent
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryItem
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryState
import com.thomaskioko.tvmaniac.presentation.watchlist.LoadingShows
import com.thomaskioko.tvmaniac.resources.R

data object LibraryScreen : Screen {
    @Composable
    override fun Content() {
    }
}

@Composable
private fun LibraryScreen(
    state: LibraryState,
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

                is LibraryContent -> {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FollowingGridContent(
    list: List<LibraryItem>,
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
            modifier = Modifier
                .animateItemPlacement(),
            posterImageUrl = show.posterImageUrl,
            title = show.title,
            onClick = { onItemClicked(show.traktId) },
        )
    }
}

@ThemePreviews
@Composable
private fun LibraryScreenPreview(
    @PreviewParameter(FollowingPreviewParameterProvider::class)
    state: LibraryState,
) {
    TvManiacTheme {
        Surface {
            LibraryScreen(
                state = state,
                onShowClicked = {},
            )
        }
    }
}
