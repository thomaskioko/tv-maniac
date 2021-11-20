package com.thomaskioko.showdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.thomaskioko.showdetails.DetailUiEffect.WatchlistError
import com.thomaskioko.showdetails.ShowDetailAction.UpdateWatchlist
import com.thomaskioko.tvmaniac.compose.R
import com.thomaskioko.tvmaniac.compose.components.CollapsableAppBar
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.RowSpacer
import com.thomaskioko.tvmaniac.compose.components.TabItem.Casts
import com.thomaskioko.tvmaniac.compose.components.TabItem.Episodes
import com.thomaskioko.tvmaniac.compose.components.TabItem.Similar
import com.thomaskioko.tvmaniac.compose.components.Tabs
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.compose.util.copy
import com.thomaskioko.tvmaniac.interactor.EpisodeQuery
import com.thomaskioko.tvmaniac.interactor.UpdateShowParams
import com.thomaskioko.tvmaniac.presentation.model.GenreModel
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import kotlinx.coroutines.flow.collect

@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailsViewModel,
    navigateUp: () -> Unit
) {

    val viewState by rememberFlowWithLifecycle(viewModel.uiStateFlow)
        .collectAsState(initial = ShowDetailViewState.Empty)

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(viewModel) {
        viewModel.uiEffects.collect {
            when (it) {
                is WatchlistError -> scaffoldState.snackbarHostState.showSnackbar(it.errorMessage)
            }
        }
    }

    val listState = rememberLazyListState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            var appBarHeight by remember { mutableStateOf(0) }
            val showAppBarBackground by remember {
                derivedStateOf {
                    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                    when {
                        visibleItemsInfo.isEmpty() -> false
                        appBarHeight <= 0 -> false
                        else -> {
                            val firstVisibleItem = visibleItemsInfo[0]
                            when {
                                // If the first visible item is > 0, we want to show the app bar background
                                firstVisibleItem.index > 0 -> true
                                // If the first item is visible, only show the app bar background once the only
                                // remaining part of the item is <= the app bar
                                else -> firstVisibleItem.size + firstVisibleItem.offset <= appBarHeight
                            }
                        }
                    }
                }
            }

            CollapsableAppBar(
                title = viewState.tvShow.title,
                showAppBarBackground = showAppBarBackground,
                onNavIconPressed = navigateUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { appBarHeight = it.height }
            )
        },
        content = { contentPadding ->
            TvShowDetailsScrollingContent(
                detailUiState = viewState,
                listState = listState,
                onSeasonSelected = { viewModel.submitAction(ShowDetailAction.SeasonSelected(it)) },
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
                onWatchlistClick = { viewModel.submitAction(UpdateWatchlist(it)) }
            )
        }
    )
}

@Composable
private fun TvShowDetailsScrollingContent(
    detailUiState: ShowDetailViewState,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onSeasonSelected: (EpisodeQuery) -> Unit,
    onWatchlistClick: (UpdateShowParams) -> Unit,
    contentPadding: PaddingValues,
) {

    LazyColumn(
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
        modifier = modifier
    ) {

        item { TvShowHeaderView(detailUiState, listState, onWatchlistClick) }

        item { SeasonTabs(detailUiState, onSeasonSelected) }

    }
}

@Composable
fun TvShowHeaderView(
    detailUiState: ShowDetailViewState,
    listState: LazyListState,
    onWatchlistClick: (UpdateShowParams) -> Unit,
) {

    val surfaceGradient = backgroundGradient().reversed()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
            .clipToBounds()
            .offset {
                IntOffset(
                    x = 0,
                    y = if (listState.firstVisibleItemIndex == 0) {
                        listState.firstVisibleItemScrollOffset / 2
                    } else 0
                )
            }) {

        Box {
            KenBurnsViewImage(
                imageUrl = detailUiState.tvShow.backdropImageUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
            )

            BoxWithConstraints {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                surfaceGradient,
                                0F,
                                constraints.maxHeight.toFloat(),
                                TileMode.Clamp
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        TvShowInfo(
                            detailUiState = detailUiState,
                            onWatchlistClick = onWatchlistClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TvShowInfo(
    detailUiState: ShowDetailViewState,
    onWatchlistClick: (UpdateShowParams) -> Unit,
) {

    val show = detailUiState.tvShow

    ColumnSpacer(16)
    val padding = Modifier.padding(horizontal = 16.dp)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = show.title,
            style = MaterialTheme.typography.h4,
            modifier = padding,
            maxLines = 1
        )

        ColumnSpacer(8)

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = show.overview,
                style = MaterialTheme.typography.body2,
                maxLines = 4,
                modifier = padding
            )
        }

        ColumnSpacer(8)

    }

    TvShowMetadata(
        show = show,
        seasons = detailUiState.tvSeasons,
        genreList = detailUiState.genreList,
        onWatchlistClick = onWatchlistClick,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(Modifier.height(16.dp))
}

@Composable
fun TvShowMetadata(
    show: TvShow,
    seasons: List<Season>,
    genreList: List<GenreModel>,
    modifier: Modifier,
    onWatchlistClick: (UpdateShowParams) -> Unit,
) {
    val resources = LocalContext.current.resources

    val divider = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            color = MaterialTheme.colors.secondary
        )
        withStyle(tagStyle) {
            append("  •  ")
        }

    }
    val text = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            background = MaterialTheme.colors.primary.copy(alpha = 0.8f)
        )

        if (show.status.isNotBlank()) {
            withStyle(tagStyle) {
                append(" ")
                append(show.status)
                append(" ")
            }
            append(divider)
        }
        append(show.year)
        append(divider)
        append(resources.getQuantityString(R.plurals.season_count, seasons.size, seasons.size))
        append(divider)
        append(show.language.uppercase())
        append(divider)
        append("${show.averageVotes}")
        append(divider)
    }
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = modifier
        )
    }

    ColumnSpacer(8)

    GenreText(genreList, show.genreIds)

    ColumnSpacer(8)

    ShowDetailButtons(
        show = show,
        onWatchlistClick = onWatchlistClick
    )

}


@Composable
private fun GenreText(
    genreList: List<GenreModel>,
    genreIds: List<Int>,
) {

    val result = genreList.filter { genre ->
        genreIds.any { id -> genre.id == id }
    }

    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        items(result) { item ->
            RowSpacer(4)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.onBackground,
                        backgroundColor = Color(0xFF414141)
                    ),
                    onClick = {}
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }

}


@Composable
fun ShowDetailButtons(
    show: TvShow,
    onWatchlistClick: (UpdateShowParams) -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        ExtendedFloatingActionButton(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_trailer_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary.copy(alpha = 0.8F)),
                )
            },
            text = {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(id = R.string.btn_trailer),
                        style = MaterialTheme.typography.body2,
                    )
                }
            },
            backgroundColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(),
            onClick = {},
            modifier = Modifier
                .padding(2.dp)
                .border(1.dp, Color(0xFF414141), RoundedCornerShape(8.dp))
        )

        RowSpacer(value = 8)

        val message = if (show.isInWatchlist)
            stringResource(id = R.string.btn_remove_watchlist)
        else stringResource(id = R.string.btn_add_watchlist)

        val imageVector = if (show.isInWatchlist)
            painterResource(id = R.drawable.ic_baseline_check_box_24)
        else painterResource(id = R.drawable.ic_baseline_add_box_24)

        ExtendedFloatingActionButton(
            icon = {
                Image(
                    painter = imageVector,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary.copy(alpha = 0.8F)),
                )
            },
            text = {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            },
            backgroundColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(),
            onClick = { onWatchlistClick(UpdateShowParams(show.id, !show.isInWatchlist)) },
            modifier = Modifier
                .padding(2.dp)
                .border(1.dp, Color(0xFF414141), RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun SeasonTabs(viewState: ShowDetailViewState, onSeasonSelected: (EpisodeQuery) -> Unit) {

    Column {
        val tabs = listOf(Episodes, Casts, Similar)

        val pagerState = rememberPagerState()

        Tabs(tabs = tabs, pagerState = pagerState)

        HorizontalPager(
            count = tabs.size,
            state = pagerState
        ) { page ->
            when (tabs[page]) {
                Casts -> SeasonCastScreen()
                Episodes -> EpisodesScreen(viewState, onSeasonSelected)
                Similar -> SimilarShowsScreen()
            }
        }
    }

}