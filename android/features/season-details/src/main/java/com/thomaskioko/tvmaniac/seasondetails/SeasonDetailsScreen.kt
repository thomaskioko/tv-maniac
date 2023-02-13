package com.thomaskioko.tvmaniac.seasondetails

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.FullScreenLoading
import com.thomaskioko.tvmaniac.compose.components.RowSpacer
import com.thomaskioko.tvmaniac.compose.components.SwipeDismissSnackbar
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.util.copy
import com.thomaskioko.tvmaniac.compose.util.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.api.Loading
import com.thomaskioko.tvmaniac.seasondetails.api.LoadingError
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.seasondetails.api.model.Episode
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetails
import com.thomaskioko.tvmaniac.seasondetails.components.WatchlistRowItem
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@Composable
fun SeasonDetailsScreen(
    viewModel: SeasonDetailsViewModel,
    navigateUp: () -> Unit,
    initialSeasonName: String? = null,
    onEpisodeClicked: (Long) -> Unit = {}
) {

    val viewState by viewModel.state.collectAsStateWithLifecycle()

    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                title = (viewState as? SeasonDetailsLoaded)?.showTitle ?: "",
                navigateUp = navigateUp
            )
        },
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .statusBarsPadding(),
        snackbarHost = { snackBarHostState ->
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { snackBarData ->
                    SwipeDismissSnackbar(
                        data = snackBarData,
                        onDismiss = { }
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )
        },
        content = { contentPadding ->
            when (viewState) {
                Loading -> FullScreenLoading()
                is LoadingError -> ErrorUi(
                    errorMessage = (viewState as LoadingError).message,
                    onRetry = {},
                )
                is SeasonDetailsLoaded -> {
                    SeasonsScrollingContent(
                        seasonsEpList = (viewState as SeasonDetailsLoaded).episodeList,
                        initialSeasonName = initialSeasonName,
                        onEpisodeClicked = onEpisodeClicked,
                        listState = listState,
                        contentPadding = contentPadding,
                    )
                }
            }
        }
    )
}


@Composable
private fun TopBar(
    title: String,
    navigateUp: () -> Unit
) {
    TvManiacTopBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = navigateUp,
                modifier = Modifier.iconButtonBackgroundScrim()
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
private fun SeasonsScrollingContent(
    seasonsEpList: List<SeasonDetails>?,
    initialSeasonName: String?,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onEpisodeClicked: (Long) -> Unit = {}
) {
    seasonsEpList?.let {
        val initialIndex = seasonsEpList
            .indexOfFirst { it.seasonName == initialSeasonName }

        LaunchedEffect(initialIndex) {
            if (initialIndex in 0 until seasonsEpList.count()) {
                listState.animateScrollToItem(index = initialIndex)
            }
        }

        val collapsedState = remember(seasonsEpList) {
            seasonsEpList.map {
                it.seasonName != initialSeasonName
            }.toMutableStateList()
        }

        LazyColumn(
            state = listState,
            contentPadding = contentPadding.copy(copyTop = false),
            modifier = Modifier.fillMaxWidth()
        ) {

            item { ColumnSpacer(16) }

            item { WatchNextRow(seasonsEpList.firstOrNull()?.episodes) }

            item { ColumnSpacer(16) }

            item { AllSeasonsTitle(seasonsEpList) }

            itemsIndexed(seasonsEpList) { index, season ->
                SeasonEpisodeList(
                    collapsedState = collapsedState,
                    index = index,
                    season = season,
                    onEpisodeClicked = onEpisodeClicked,
                )
            }
        }
    }
}

@Composable
private fun AllSeasonsTitle(seasonsEpList: List<SeasonDetails>) {
    seasonsEpList.firstOrNull()?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            ColumnSpacer(8)

            Text(
                text = stringResource(id = R.string.title_all_seasons),
                style = MaterialTheme.typography.caption.copy(MaterialTheme.colors.secondary),
            )
        }
        ColumnSpacer(8)
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
private fun WatchNextRow(
    episodeList: List<Episode>?
) {
    episodeList?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            ColumnSpacer(8)

            Text(
                text = stringResource(id = R.string.title_watch_next),
                style = MaterialTheme.typography.caption.copy(MaterialTheme.colors.secondary),
            )
        }

        ColumnSpacer(8)

        val lazyListState = rememberLazyListState()

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {

            itemsIndexed(episodeList) { index, episode ->
                RowSpacer(if (index == 0) 32 else 8)

                WatchlistRowItem(
                    episode = episode,
                    onEpisodeClicked = {},
                )
            }

            item { RowSpacer(16) }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SeasonsContentPreview() {
    TvManiacTheme {
        Surface {
            SeasonsScrollingContent(
                seasonsEpList = seasonsEpList,
                initialSeasonName = "Specials",
                onEpisodeClicked = {},
                contentPadding = PaddingValues(),
                listState = LazyListState()
            )
        }
    }
}
