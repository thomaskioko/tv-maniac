package com.thomaskioko.showdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.showdetails.DetailConstants.HEADER_HEIGHT
import com.thomaskioko.tvmaniac.compose.components.CircularLoadingView
import com.thomaskioko.tvmaniac.compose.components.CollapsableAppBar
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.RowSpacer
import com.thomaskioko.tvmaniac.compose.components.SnackBarErrorRetry
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacChip
import com.thomaskioko.tvmaniac.compose.components.TvManiacOutlinedButton
import com.thomaskioko.tvmaniac.compose.components.TvManiacTextButton
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.data.showdetails.DismissWebViewError
import com.thomaskioko.tvmaniac.data.showdetails.FollowShow
import com.thomaskioko.tvmaniac.data.showdetails.SeasonState
import com.thomaskioko.tvmaniac.data.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.data.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.data.showdetails.ShowState
import com.thomaskioko.tvmaniac.data.showdetails.SimilarShowsState
import com.thomaskioko.tvmaniac.data.showdetails.TrailersState
import com.thomaskioko.tvmaniac.data.showdetails.WebViewError
import com.thomaskioko.tvmaniac.data.showdetails.model.Season
import com.thomaskioko.tvmaniac.data.showdetails.model.Show
import com.thomaskioko.tvmaniac.resources.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailsViewModel,
    navigateUp: () -> Unit,
    onShowClicked: (Long) -> Unit,
    onSeasonClicked: (Long, String) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit = { _, _ -> }
) {

    val viewState by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val title = (viewState as? ShowState.ShowLoaded)?.show?.title ?: ""

    Scaffold(
        topBar = {
            ShowTopBar(
                listState = listState,
                title = title,
                onNavUpClick = navigateUp
            )
        },
        content = { contentPadding ->

            when (viewState) {
                ShowDetailsState.Loading -> CircularLoadingView()
                is ShowDetailsState.ShowDetailsError -> ErrorUi(
                    errorMessage = (viewState as ShowDetailsState.ShowDetailsError).errorMessage,
                    onRetry = {}
                )

                is ShowDetailsState.ShowDetailsLoaded -> {
                    ShowDetailContent(
                        contentPadding = contentPadding,
                        snackBarHostState = snackbarHostState,
                        viewState = viewState as ShowDetailsState.ShowDetailsLoaded,
                        listState = listState,
                        onSeasonClicked = onSeasonClicked,
                        onShowClicked = onShowClicked,
                        onWatchTrailerClicked = { canPlay, traktId, trailerKey ->
                            if (canPlay)
                                onWatchTrailerClicked(traktId, trailerKey)
                            else
                                viewModel.dispatch(WebViewError)
                        },
                        onUpdateFavoriteClicked = { viewModel.dispatch(it) },
                        onDismissTrailerErrorClicked = { viewModel.dispatch(DismissWebViewError) }
                    )
                }
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
            .onSizeChanged { appBarHeight = it.height }
    )
}

@Composable
private fun ShowDetailContent(
    listState: LazyListState,
    snackBarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    viewState: ShowDetailsState.ShowDetailsLoaded,
    onSeasonClicked: (Long, String) -> Unit,
    onShowClicked: (Long) -> Unit,
    onWatchTrailerClicked: (Boolean, Long, String?) -> Unit,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
    onDismissTrailerErrorClicked: () -> Unit,
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
    ) {

        item {
            val trailerState = (viewState.trailerState as? TrailersState.TrailersLoaded)

            ShowHeaderContent(
                listState = listState,
                showState = viewState.showState,
                trailerKey = trailerState?.trailersList?.firstOrNull()?.key,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked
            ) { showId, key ->
                val hasWebView = trailerState?.hasWebViewInstalled ?: false
                onWatchTrailerClicked(hasWebView, showId, key)
            }
        }

        item {
            when (viewState.seasonState) {
                is SeasonState.SeasonsLoaded -> {
                    val state = (viewState.seasonState as SeasonState.SeasonsLoaded)
                    SeasonsContent(
                        isLoading = state.isLoading,
                        seasonsList = state.seasonsList,
                        onSeasonClicked = onSeasonClicked
                    )
                }

                is SeasonState.SeasonsError -> {
                    //TODO:: Show Error view with retry
                }
            }
        }

        item {
            TrailersContent(
                trailersState = viewState.trailerState,
                snackBarHostState = snackBarHostState,
                onDismissTrailerErrorClicked = onDismissTrailerErrorClicked,
                onWatchTrailerClicked = onWatchTrailerClicked
            )
        }

        item {
            when (viewState.similarShowsState) {
                is SimilarShowsState.SimilarShowsError -> {
                    //TODO:: Show Error view with retry
                }

                is SimilarShowsState.SimilarShowsLoaded -> {
                    val state =
                        viewState.similarShowsState as SimilarShowsState.SimilarShowsLoaded
                    SimilarShowsContent(
                        isLoading = state.isLoading,
                        similarShows = state.similarShows,
                        onShowClicked = onShowClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowHeaderContent(
    showState: ShowState,
    trailerKey: String?,
    listState: LazyListState,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) {
    when (showState) {
        is ShowState.ShowError -> {
            ErrorUi(
                errorMessage = showState.errorMessage,
                onRetry = {}
            )
        }

        is ShowState.ShowLoaded -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HEADER_HEIGHT)
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

                KenBurnsViewImage(
                    imageUrl = showState.show.backdropImageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HEADER_HEIGHT)
                        .clipToBounds()
                )

                Body(
                    show = showState.show,
                    trailerKey = trailerKey,
                    onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                    onWatchTrailerClicked = onWatchTrailerClicked
                )
            }
        }
    }

}

@Composable
private fun Body(
    show: Show,
    trailerKey: String?,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) {
    val surfaceGradient = backgroundGradient().reversed()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HEADER_HEIGHT)
            .clipToBounds()
            .background(Brush.verticalGradient(surfaceGradient))
            .padding(horizontal = 16.dp)
    ) {
        ColumnSpacer(16)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = show.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            ColumnSpacer(8)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                ExpandingText(
                    text = show.overview,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            ColumnSpacer(8)

            TvShowMetadata(
                releaseYear = show.year,
                status = show.status,
                seasonNumber = show.numberOfSeasons,
                language = show.language,
                rating = show.rating
            )

            GenreText(show.genres)

            ColumnSpacer(8)

            ShowDetailButtons(
                traktId = show.traktId,
                trailerKey = trailerKey,
                isFollowed = show.isFollowed,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = onWatchTrailerClicked
            )

            ColumnSpacer(8)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun TvShowMetadata(
    releaseYear: String,
    status: String?,
    seasonNumber: Int?,
    language: String?,
    rating: Double
) {
    val resources = LocalContext.current.resources

    val divider = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
            color = MaterialTheme.colorScheme.secondary
        )
        withStyle(tagStyle) {
            append("  •  ")
        }
    }
    val text = buildAnnotatedString {
        val statusStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
            color = MaterialTheme.colorScheme.secondary,
            background = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
        )

        val tagStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
            color = MaterialTheme.colorScheme.onSurface,
        )

        AnimatedVisibility(visible = !status.isNullOrBlank()) {
            status?.let {
                withStyle(statusStyle) {
                    append(" ")
                    append(it)
                    append(" ")
                }
                append(divider)
            }
        }

        withStyle(tagStyle) {
            append(releaseYear)
        }

        AnimatedVisibility(visible = seasonNumber != null) {
            seasonNumber?.let {
                append(divider)
                withStyle(tagStyle) {
                    append(resources.getQuantityString(R.plurals.season_count, it, it))
                }
            }
        }

        append(divider)
        language?.let { language ->
            withStyle(tagStyle) {
                append(language)
            }
            append(divider)
        }
        withStyle(tagStyle) {
            append("$rating")
        }
        append(divider)
    }

    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

    ColumnSpacer(8)

}

@Composable
private fun GenreText(genreList: List<String>) {

    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(genreList) { genre ->
            RowSpacer(4)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                TvManiacTextButton(
                    onClick = {},
                    shape = RoundedCornerShape(4.dp),
                    buttonColors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                    ),
                    content = {
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ShowDetailButtons(
    isFollowed: Boolean,
    traktId: Long,
    trailerKey: String?,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.Center,
    ) {

        TvManiacOutlinedButton(
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_trailer_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.8F
                        )
                    ),
                )
            },
            text = stringResource(id = R.string.btn_trailer),
            textPadding = 8.dp,
            borderColor = MaterialTheme.colorScheme.secondary,
            onClick = { onWatchTrailerClicked(traktId, trailerKey) },
        )

        RowSpacer(value = 8)


        TvManiacOutlinedButton(
            leadingIcon = {
                Image(
                    painter = if (isFollowed)
                        painterResource(id = R.drawable.ic_baseline_check_box_24)
                    else
                        painterResource(id = R.drawable.ic_baseline_add_box_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.8F
                        )
                    ),
                )
            },
            text = if (isFollowed)
                stringResource(id = R.string.unfollow)
            else stringResource(id = R.string.following),
            textPadding = 8.dp,
            onClick = {
                onUpdateFavoriteClicked(
                    FollowShow(
                        traktId = traktId,
                        addToWatchList = isFollowed,
                    )
                )
            },
            borderColor = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun SeasonsContent(
    isLoading: Boolean,
    seasonsList: List<Season>,
    onSeasonClicked: (Long, String) -> Unit
) {
    TextLoadingItem(
        isLoading = isLoading,
        text = stringResource(id = R.string.title_seasons)
    )

    val selectedIndex by remember { mutableStateOf(0) }

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        divider = {}, /* Disable the built-in divider */
        indicator = {},
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        seasonsList.forEach { season ->
            Tab(
                modifier = Modifier
                    .padding(end = 4.dp),
                selected = true,
                onClick = {
                    onSeasonClicked(
                        season.tvShowId,
                        season.name
                    )
                }
            ) {
                TvManiacChip(
                    text = season.name,
                    onClick = {
                        onSeasonClicked(
                            season.tvShowId,
                            season.name
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun TrailersContent(
    trailersState: TrailersState,
    snackBarHostState: SnackbarHostState,
    onDismissTrailerErrorClicked: () -> Unit,
    onWatchTrailerClicked: (Boolean, Long, String?) -> Unit
) {
    when (trailersState) {
        is TrailersState.TrailersError -> {
            //TODO:: Show Error view with retry
        }

        is TrailersState.TrailersLoaded -> {

            SnackBarErrorRetry(
                snackBarHostState = snackBarHostState,
                errorMessage = trailersState.playerErrorMessage,
                onErrorAction = onDismissTrailerErrorClicked,
                actionLabel = "Dismiss"
            )

            TrailersRowContent(
                isLoading = trailersState.isLoading,
                trailersList = trailersState.trailersList,
                onTrailerClicked = { showId, videoKey ->
                    onWatchTrailerClicked(
                        trailersState.hasWebViewInstalled,
                        showId,
                        videoKey
                    )
                }
            )
        }
    }
}

private object DetailConstants {
     val HEADER_HEIGHT = 550.dp
}


@ThemePreviews
@Composable
fun ShowDetailContentPreview() {
    TvManiacTheme {
        Surface {
            val snackbarHostState = remember { SnackbarHostState() }
            ShowDetailContent(
                snackBarHostState = snackbarHostState,
                viewState = detailUiState,
                listState = LazyListState(),
                contentPadding = PaddingValues(),
                onSeasonClicked = { _, _ -> },
                onShowClicked = {},
                onWatchTrailerClicked = { _, _, _ -> },
                onUpdateFavoriteClicked = {},
                onDismissTrailerErrorClicked = {}
            )
        }
    }
}
