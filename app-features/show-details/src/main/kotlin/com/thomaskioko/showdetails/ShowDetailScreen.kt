package com.thomaskioko.showdetails

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.thomaskioko.showdetails.ShowDetailAction.UpdateWatchlist
import com.thomaskioko.tvmaniac.compose.components.CollapsableAppBar
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.RowSpacer
import com.thomaskioko.tvmaniac.compose.components.TabItem.Casts
import com.thomaskioko.tvmaniac.compose.components.TabItem.Episodes
import com.thomaskioko.tvmaniac.compose.components.TabItem.Similar
import com.thomaskioko.tvmaniac.compose.components.Tabs
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.compose.util.copy
import com.thomaskioko.tvmaniac.discover.api.interactor.UpdateShowParams
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel
import com.thomaskioko.tvmaniac.episodes.api.EpisodeQuery
import com.thomaskioko.tvmaniac.episodes.api.EpisodeUiModel
import com.thomaskioko.tvmaniac.genre.api.GenreUIModel
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import kotlinx.coroutines.InternalCoroutinesApi

private val HeaderHeight = 550.dp

@OptIn(InternalCoroutinesApi::class)
@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailsViewModel,
    navigateUp: () -> Unit
) {

    val viewState by rememberFlowWithLifecycle(viewModel.observeState())
        .collectAsState(initial = ShowDetailViewState.Empty)

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(viewModel) {
        viewModel.observeSideEffect().collect {
            when (it) {
                is ShowDetailEffect.WatchlistError ->
                    scaffoldState.snackbarHostState
                        .showSnackbar(it.errorMessage)
                is ShowDetailEffect.ShowDetailsError ->
                    scaffoldState.snackbarHostState
                        .showSnackbar(it.errorMessage)
            }
        }
    }

    val listState = rememberLazyListState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ShowTopBar(
                listState = listState,
                title = viewState.showUiModel.title,
                onNavUpClick = navigateUp
            )
        },
        content = { contentPadding ->

            Surface(modifier = Modifier.fillMaxWidth()) {
                TvShowDetailsScrollingContent(
                    detailUiState = viewState,
                    listState = listState,
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize(),
                    onWatchlistClick = { viewModel.dispatch(UpdateWatchlist(it)) },
                    onSeasonSelected = { viewModel.dispatch(ShowDetailAction.SeasonSelected(it)) }
                )
            }
        }
    )
}

@Composable
private fun ShowTopBar(
    listState: LazyListState,
    title: String,
    onNavUpClick: () -> Unit
) {
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
                        else -> firstVisibleItem.size + firstVisibleItem.offset - 5 <= appBarHeight
                    }
                }
            }
        }
    }

    CollapsableAppBar(
        title = title,
        showAppBarBackground = showAppBarBackground,
        onNavIconPressed = onNavUpClick,
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                appBarHeight = it.height
            }
    )
}

@Composable
private fun TvShowDetailsScrollingContent(
    detailUiState: ShowDetailViewState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onSeasonSelected: (EpisodeQuery) -> Unit = {},
    onWatchlistClick: (UpdateShowParams) -> Unit = {},
) {

    LazyColumn(
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
        modifier = modifier
    ) {

        item {
            HeaderViewContent(
                detailUiState = detailUiState,
                listState = listState,
                onWatchlistClick = onWatchlistClick
            )
        }

        item {
            SeasonTabs(
                isLoading = detailUiState.isLoading,
                tvSeasonUiModels = detailUiState.tvSeasonUiModels,
                episodeList = detailUiState.episodeList,
                onSeasonSelected = onSeasonSelected
            )
        }
    }
}

@Composable
private fun HeaderViewContent(
    detailUiState: ShowDetailViewState,
    listState: LazyListState,
    onWatchlistClick: (UpdateShowParams) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .clipToBounds()
            .offset {
                IntOffset(
                    x = 0,
                    y = if (listState.firstVisibleItemIndex == 0) {
                        listState.firstVisibleItemScrollOffset / 2
                    } else 0
                )
            }
    ) {
        HeaderImage(
            backdropImageUrl = detailUiState.showUiModel.backdropImageUrl
        )

        Body(
            showUiModel = detailUiState.showUiModel,
            seasonUiModels = detailUiState.tvSeasonUiModels,
            genreUIS = detailUiState.genreUIList,
            onWatchlistClick
        )
    }
}

@Composable
private fun HeaderImage(backdropImageUrl: String) {
    KenBurnsViewImage(
        imageUrl = backdropImageUrl,
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .clipToBounds()
    )
}

@Composable
private fun Body(
    showUiModel: ShowUiModel,
    seasonUiModels: List<SeasonUiModel>,
    genreUIS: List<GenreUIModel>,
    onWatchlistClick: (UpdateShowParams) -> Unit
) {
    val surfaceGradient = backgroundGradient().reversed()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .clipToBounds()
            .background(Brush.verticalGradient(surfaceGradient))
    ) {
        ColumnSpacer(16)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = showUiModel.title,
                style = MaterialTheme.typography.h4,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            ColumnSpacer(8)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                ExpandingText(
                    text = showUiModel.overview,
                )
            }

            ColumnSpacer(8)

            TvShowMetadata(
                showUiModel = showUiModel,
                seasonUiModels = seasonUiModels,
                genreUIList = genreUIS,
                onWatchlistClick = onWatchlistClick,
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun TvShowMetadata(
    showUiModel: ShowUiModel,
    seasonUiModels: List<SeasonUiModel>,
    genreUIList: List<GenreUIModel>,
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
            color = MaterialTheme.colors.secondary,
            background = MaterialTheme.colors.secondary.copy(alpha = 0.08f)
        )

        if (showUiModel.status.isNotBlank()) {
            withStyle(tagStyle) {
                append(" ")
                append(showUiModel.status)
                append(" ")
            }
            append(divider)
        }
        append(showUiModel.year)
        append(divider)
        append(
            resources.getQuantityString(
                R.plurals.season_count,
                seasonUiModels.size,
                seasonUiModels.size
            )
        )
        append(divider)
        append(showUiModel.language.uppercase())
        append(divider)
        append("${showUiModel.averageVotes}")
        append(divider)
    }

    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.fillMaxWidth()
        )
    }

    ColumnSpacer(8)

    GenreText(genreUIList, showUiModel.genreIds)

    ColumnSpacer(8)

    ShowDetailButtons(
        showUiModel = showUiModel,
        onWatchlistClick = onWatchlistClick
    )
}

@Composable
private fun GenreText(
    genreUIList: List<GenreUIModel>,
    genreIds: List<Int>,
) {

    val result = genreUIList.filter { genre ->
        genreIds.any { id -> genre.id == id }
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(result) { item ->
            RowSpacer(4)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.onBackground,
                        backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.08f)
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
    showUiModel: ShowUiModel,
    onWatchlistClick: (UpdateShowParams) -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.Center,
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
            shape = RectangleShape,
            backgroundColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = {},
            modifier = Modifier
                .padding(2.dp)
                .border(1.dp, Color(0xFF414141), RoundedCornerShape(8.dp))
        )

        RowSpacer(value = 8)

        val message = if (showUiModel.isInWatchlist)
            stringResource(id = R.string.btn_remove_watchlist)
        else stringResource(id = R.string.btn_add_watchlist)

        val imageVector = if (showUiModel.isInWatchlist)
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
            shape = RectangleShape,
            backgroundColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = {
                onWatchlistClick(UpdateShowParams(showUiModel.id, !showUiModel.isInWatchlist))
            },
            modifier = Modifier
                .padding(2.dp)
                .border(1.dp, Color(0xFF414141), RoundedCornerShape(8.dp))
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SeasonTabs(
    isLoading: Boolean,
    tvSeasonUiModels: List<SeasonUiModel>,
    episodeList: List<EpisodeUiModel>,
    onSeasonSelected: (EpisodeQuery) -> Unit = {}
) {

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
                Episodes -> EpisodesScreen(
                    isLoading,
                    tvSeasonUiModels,
                    episodeList,
                    onSeasonSelected
                )
                Similar -> SimilarShowsScreen()
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TvShowDetailsScrollingPreview() {
    TvManiacTheme {
        Surface {
            TvShowDetailsScrollingContent(
                detailUiState = detailUiState,
                listState = LazyListState(),
                contentPadding = PaddingValues(),
            )
        }
    }
}
