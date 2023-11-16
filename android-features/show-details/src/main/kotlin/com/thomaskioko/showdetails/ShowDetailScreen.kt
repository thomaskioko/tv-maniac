package com.thomaskioko.showdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.showdetails.DetailConstants.HEADER_HEIGHT
import com.thomaskioko.tvmaniac.common.localization.MR
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.CollapsableAppBar
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.SnackBarErrorRetry
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacChip
import com.thomaskioko.tvmaniac.compose.components.TvManiacOutlinedButton
import com.thomaskioko.tvmaniac.compose.components.TvManiacTextButton
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.navigation.extensions.viewModel
import com.thomaskioko.tvmaniac.presentation.showdetails.DismissWebViewError
import com.thomaskioko.tvmaniac.presentation.showdetails.FollowShowClicked
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.presentation.showdetails.WebViewError
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias ShowDetail = @Composable (
    onBackClicked: () -> Unit,
    onShowClicked: (Long) -> Unit,
    onSeasonClicked: (Long, String) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) -> Unit

@Inject
@Composable
fun ShowDetail(
    viewModelFactory: (SavedStateHandle) -> ShowDetailsViewModel,
    @Assisted onBackClicked: () -> Unit,
    @Assisted onShowClicked: (Long) -> Unit,
    @Assisted onSeasonClicked: (Long, String) -> Unit,
    @Assisted onWatchTrailerClicked: (Long, String?) -> Unit = { _, _ -> },
) {
    ShowDetailScreen(
        viewModel = viewModel(factory = viewModelFactory),
        onBackClicked = onBackClicked,
        onSeasonClicked = onSeasonClicked,
        onShowClicked = onShowClicked,
        onWatchTrailerClicked = onWatchTrailerClicked,
    )
}

@Composable
internal fun ShowDetailScreen(
    viewModel: ShowDetailsViewModel,
    onBackClicked: () -> Unit,
    onSeasonClicked: (Long, String) -> Unit,
    onShowClicked: (Long) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val title = (viewState as? ShowDetailsLoaded)?.show?.title ?: ""

    ShowDetailScreen(
        state = viewState,
        title = title,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        listState = listState,
        onBackClicked = onBackClicked,
        onSeasonClicked = onSeasonClicked,
        onShowClicked = onShowClicked,
        onWatchTrailerClicked = { canPlay, traktId, trailerKey ->
            if (canPlay) {
                onWatchTrailerClicked(traktId, trailerKey)
            } else {
                viewModel.dispatch(WebViewError)
            }
        },
        onUpdateFavoriteClicked = { viewModel.dispatch(FollowShowClicked(it)) },
        onDismissTrailerErrorClicked = { viewModel.dispatch(DismissWebViewError) },
    )
}

@Composable
internal fun ShowDetailScreen(
    state: ShowDetailsState,
    title: String,
    onBackClicked: () -> Unit,
    onSeasonClicked: (Long, String) -> Unit,
    onShowClicked: (Long) -> Unit,
    onWatchTrailerClicked: (Boolean, Long, String?) -> Unit,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    snackbarHostState: SnackbarHostState,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onDismissTrailerErrorClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            ShowTopBar(
                listState = listState,
                title = title,
                onNavUpClick = onBackClicked,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { contentPadding ->

            when (state) {
                is ShowDetailsLoaded -> ShowDetailContent(
                    show = state.show,
                    trailerContent = state.trailersContent,
                    seasonsContent = state.seasonsContent,
                    similarShowsContent = state.similarShowsContent,
                    contentPadding = contentPadding,
                    snackBarHostState = snackbarHostState,
                    listState = listState,
                    modifier = modifier,
                    onSeasonClicked = onSeasonClicked,
                    onShowClicked = onShowClicked,
                    onWatchTrailerClicked = onWatchTrailerClicked,
                    onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                    onDismissTrailerErrorClicked = onDismissTrailerErrorClicked,
                )
            }
        },
    )
}

@Composable
private fun ShowTopBar(
    listState: LazyListState,
    title: String,
    onNavUpClick: () -> Unit,
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
                        firstVisibleItem.index > 0 -> true
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
            .onSizeChanged { appBarHeight = it.height },
    )
}

@Composable
private fun ShowDetailContent(
    show: Show,
    trailerContent: ShowDetailsLoaded.TrailersContent?,
    seasonsContent: ShowDetailsLoaded.SeasonsContent?,
    similarShowsContent: ShowDetailsLoaded.SimilarShowsContent?,
    listState: LazyListState,
    snackBarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onSeasonClicked: (Long, String) -> Unit,
    onShowClicked: (Long) -> Unit,
    onWatchTrailerClicked: (Boolean, Long, String?) -> Unit,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onDismissTrailerErrorClicked: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
    ) {
        item {
            HeaderContent(
                listState = listState,
                show = show,
                trailerKey = trailerContent?.trailersList?.firstOrNull()?.key,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = { showId, key ->
                    val hasWebView = trailerContent?.hasWebViewInstalled ?: false
                    onWatchTrailerClicked(hasWebView, showId, key)
                },
            )
        }

        item {
            seasonsContent?.let {
                SeasonsContent(
                    isLoading = seasonsContent.isLoading,
                    seasonsList = seasonsContent.seasonsList,
                    onSeasonClicked = onSeasonClicked,
                )
            }
        }

        item {
            if (trailerContent != null) {
                TrailersContent(
                    trailersState = trailerContent,
                    snackBarHostState = snackBarHostState,
                    onDismissTrailerErrorClicked = onDismissTrailerErrorClicked,
                    onWatchTrailerClicked = onWatchTrailerClicked,
                )
            }
        }

        item {
            if (similarShowsContent != null) {
                SimilarShowsContent(
                    isLoading = similarShowsContent.isLoading,
                    similarShows = similarShowsContent.similarShows,
                    onShowClicked = onShowClicked,
                )
            }
        }
    }
}

@Composable
private fun HeaderContent(
    show: Show?,
    trailerKey: String?,
    listState: LazyListState,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) {
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
                    } else {
                        0
                    },
                )
            },
    ) {
        KenBurnsViewImage(
            imageUrl = show?.backdropImageUrl,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
        )

        if (show != null) {
            Body(
                show = show,
                trailerKey = trailerKey,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = onWatchTrailerClicked,
            )
        }
    }
}

@Composable
private fun Body(
    show: Show,
    trailerKey: String?,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) {
    val surfaceGradient = backgroundGradient().reversed()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Brush.verticalGradient(surfaceGradient))
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = show.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExpandingText(
                text = show.overview,
                textStyle = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShowMetadata(
                releaseYear = show.year,
                status = show.status,
                seasonNumber = show.numberOfSeasons,
                language = show.language,
                rating = show.rating,
            )

            GenreText(show.genres)

            Spacer(modifier = Modifier.height(8.dp))

            ShowDetailButtons(
                traktId = show.traktId,
                trailerKey = trailerKey,
                isFollowed = show.isFollowed,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = onWatchTrailerClicked,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ShowMetadata(
    releaseYear: String,
    status: String?,
    seasonNumber: Int?,
    language: String?,
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        val resources = LocalContext.current.resources

        val divider = buildAnnotatedString {
            val tagStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                color = MaterialTheme.colorScheme.secondary,
            )
            withStyle(tagStyle) {
                append("  •  ")
            }
        }
        val text = buildAnnotatedString {
            val statusStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                color = MaterialTheme.colorScheme.secondary,
                background = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
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
                        append(
                            resources.getQuantityString(
                                MR.plurals.season_count.resourceId,
                                it,
                                it
                            )
                        )
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

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun GenreText(
    genreList: List<String>,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(genreList) { genre ->

            Spacer(modifier = Modifier.width(4.dp))

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
                        color = MaterialTheme.colorScheme.secondary,
                    )
                },
            )
        }
    }
}

@Composable
fun ShowDetailButtons(
    isFollowed: Boolean,
    traktId: Long,
    trailerKey: String?,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        TvManiacOutlinedButton(
            leadingIcon = {
                Image(
                    imageVector = Icons.Filled.Movie,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.8F,
                        ),
                    ),
                )
            },
            text = stringResource(id = MR.strings.watch_trailer_cta.resourceId),
            textPadding = 8.dp,
            borderColor = MaterialTheme.colorScheme.secondary,
            onClick = { onWatchTrailerClicked(traktId, trailerKey) },
        )

        Spacer(modifier = Modifier.width(8.dp))

        TvManiacOutlinedButton(
            leadingIcon = {
                Image(
                    imageVector = if (isFollowed) {
                        Icons.Filled.LibraryAddCheck
                    } else {
                        Icons.Filled.LibraryAdd
                    },
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.8F,
                        ),
                    ),
                )
            },
            text = if (isFollowed) {
                stringResource(id = MR.strings.unfollow_cta.resourceId)
            } else {
                stringResource(id = MR.strings.follow_cta.resourceId)
            },
            textPadding = 8.dp,
            onClick = { onUpdateFavoriteClicked(isFollowed) },
            borderColor = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun SeasonsContent(
    isLoading: Boolean,
    seasonsList: List<Season>,
    onSeasonClicked: (Long, String) -> Unit,
) {
    if (seasonsList.isNotEmpty()) {
        TextLoadingItem(
            isLoading = isLoading,
            text = stringResource(id = MR.strings.label_browse_seasons.resourceId),
        )
        val selectedIndex by remember { mutableIntStateOf(0) }

        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            divider = {}, /* Disable the built-in divider */
            indicator = {},
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
        ) {
            seasonsList.forEach { season ->
                Tab(
                    modifier = Modifier
                        .padding(end = 4.dp),
                    selected = true,
                    onClick = {
                        onSeasonClicked(
                            season.tvShowId,
                            season.name,
                        )
                    },
                ) {
                    TvManiacChip(
                        text = season.name,
                        onClick = {
                            onSeasonClicked(
                                season.tvShowId,
                                season.name,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TrailersContent(
    trailersState: ShowDetailsLoaded.TrailersContent?,
    snackBarHostState: SnackbarHostState,
    onDismissTrailerErrorClicked: () -> Unit,
    onWatchTrailerClicked: (Boolean, Long, String?) -> Unit,
) {
    SnackBarErrorRetry(
        snackBarHostState = snackBarHostState,
        errorMessage = trailersState?.playerErrorMessage,
        onErrorAction = onDismissTrailerErrorClicked,
        actionLabel = "Dismiss",
    )

    if (trailersState != null) {
        TrailersRowContent(
            isLoading = trailersState.isLoading,
            trailersList = trailersState.trailersList,
            onTrailerClicked = { showId, videoKey ->
                onWatchTrailerClicked(
                    trailersState.hasWebViewInstalled,
                    showId,
                    videoKey,
                )
            },
        )
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SimilarShowsContent(
    isLoading: Boolean,
    similarShows: List<Show>,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    TextLoadingItem(
        isLoading = isLoading,
        text = stringResource(id = MR.strings.label_more_like_this.resourceId),
    )

    LazyRow(
        modifier = modifier,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
        itemsIndexed(similarShows) { index, tvShow ->
            val value = if (index == 0) 16 else 4

            Spacer(modifier = Modifier.width(value.dp))

            TvPosterCard(
                posterImageUrl = tvShow.posterImageUrl,
                title = tvShow.title,
                onClick = { onShowClicked(tvShow.traktId) },
                imageWidth = 84.dp,
            )
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun TrailersRowContent(
    isLoading: Boolean,
    trailersList: List<Trailer>,
    modifier: Modifier = Modifier,
    onTrailerClicked: (Long, String) -> Unit,
) {
    TextLoadingItem(
        isLoading = isLoading,
        text = stringResource(id = MR.strings.label_trailer.resourceId),
    )

    val lazyListState = rememberLazyListState()

    LazyRow(
        modifier = modifier,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
        itemsIndexed(trailersList) { index, trailer ->

            val value = if (index == 0) 16 else 8
            Spacer(modifier = Modifier.width(value.dp))

            Card(
                modifier = Modifier
                    .clickable { onTrailerClicked(trailer.showId, trailer.key) },
                shape = RoundedCornerShape(4.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp,
                ),
            ) {
                Box {
                    AsyncImageComposable(
                        model = trailer.youtubeThumbnailUrl,
                        contentDescription = trailer.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(140.dp)
                            .aspectRatio(3 / 1.5f)
                            .drawWithCache {
                                val gradient = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startY = size.height / 3,
                                    endY = size.height,
                                )
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(gradient, blendMode = BlendMode.Multiply)
                                }
                            },
                    )

                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = trailer.name,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                    )
                }
            }
        }
    }
}

private object DetailConstants {
    val HEADER_HEIGHT = 550.dp
}

@ThemePreviews
@Composable
private fun ShowDetailScreenPreview(
    @PreviewParameter(DetailPreviewParameterProvider::class)
    state: ShowDetailsState,
) {
    TvManiacTheme {
        Surface {
            ShowDetailScreen(
                state = state,
                title = "",
                onBackClicked = {},
                onSeasonClicked = { _, _ -> },
                onShowClicked = {},
                onWatchTrailerClicked = { _, _, _ -> },
                onUpdateFavoriteClicked = {},
                snackbarHostState = SnackbarHostState(),
                onDismissTrailerErrorClicked = {},
                listState = LazyListState(),
            )
        }
    }
}
