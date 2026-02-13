package com.thomaskioko.tvmaniac.seasondetails.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CastCard
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.SheetDragHandle
import com.thomaskioko.tvmaniac.compose.components.ShowLinearProgressIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacAlertDialog
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.components.actionIconWhen
import com.thomaskioko.tvmaniac.compose.extensions.contentBackgroundGradient
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.plurals.season_images_count
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_navigate_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_show_images
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_button_just_this
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_button_just_this_season
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_button_mark_all
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_button_mark_all_seasons
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_button_no
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_button_yes
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_message_episode_unwatched
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_message_mark_previous
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_message_mark_previous_seasons
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_message_unwatched
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_message_watched
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_title_episode_unwatched
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_title_mark_previous
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_title_mark_previous_seasons
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_title_unwatched
import com.thomaskioko.tvmaniac.i18n.MR.strings.dialog_title_watched
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_casts
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_season_overview
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.seasondetails.presenter.ConfirmDialogAction
import com.thomaskioko.tvmaniac.seasondetails.presenter.DismissDialog
import com.thomaskioko.tvmaniac.seasondetails.presenter.ReloadSeasonDetails
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsAction
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsBackClicked
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsMessageShown
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDialogState
import com.thomaskioko.tvmaniac.seasondetails.presenter.SecondaryDialogAction
import com.thomaskioko.tvmaniac.seasondetails.presenter.ShowGallery
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.Cast
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonImagesModel
import com.thomaskioko.tvmaniac.seasondetails.ui.components.CollapsableContent
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

@Composable
public fun SeasonDetailsScreen(
    presenter: SeasonDetailsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = state.message) {
        val message = state.message
        if (message != null && !state.showError) {
            val result = snackBarHostState.showSnackbar(
                message = message.message,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Short,
            )
            when (result) {
                SnackbarResult.ActionPerformed,
                SnackbarResult.Dismissed,
                -> presenter.dispatch(SeasonDetailsMessageShown(message.id))
            }
        }
    }

    SeasonDetailsScreen(
        modifier = modifier,
        state = state,
        snackBarHostState = snackBarHostState,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun SeasonDetailsScreen(
    state: SeasonDetailsModel,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (SeasonDetailsAction) -> Unit,
) {
    val listState = rememberLazyListState()

    TvManiacBottomSheetScaffold(
        modifier = modifier,
        showBottomSheet = state.isGalleryVisible,
        sheetContent = { ImageGalleryContent(imageList = state.seasonImages) },
        onDismissBottomSheet = { onAction(DismissDialog) },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        sheetDragHandle = {
            val title = stringResource(cd_show_images.resourceId, state.seasonName)
            SheetDragHandle(
                title = title,
                imageVector = Icons.Outlined.KeyboardArrowDown,
                onClick = { onAction(DismissDialog) },
            )
        },
        content = { contentPadding ->
            Box(Modifier.fillMaxSize()) {
                if (state.showError) {
                    ErrorUi(
                        errorIcon = {
                            Image(
                                modifier = Modifier.size(120.dp),
                                imageVector = Icons.Outlined.ErrorOutline,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F)),
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = state.message?.message,
                        onRetry = { onAction(ReloadSeasonDetails) },
                    )
                } else {
                    LazyColumnContent(
                        seasonDetailsModel = state,
                        isLoading = state.isRefreshing,
                        contentPadding = contentPadding,
                        onAction = onAction,
                        listState = listState,
                    )
                }

                RefreshCollapsableTopAppBar(
                    listState = listState,
                    title = {
                        Text(
                            text = state.seasonName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = cd_navigate_back.resolve(LocalContext.current),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    actionIcon = actionIconWhen(state.message != null) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    isRefreshing = state.isRefreshing,
                    onNavIconClicked = { onAction(SeasonDetailsBackClicked) },
                    onActionIconClicked = { onAction(ReloadSeasonDetails) },
                )
            }
        },
    )
}

@Composable
internal fun LazyColumnContent(
    listState: LazyListState,
    isLoading: Boolean,
    seasonDetailsModel: SeasonDetailsModel,
    contentPadding: PaddingValues,
    onAction: (SeasonDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
    ) {
        item {
            HeaderContent(
                scrollState = scrollState,
                imageUrl = seasonDetailsModel.imageUrl,
                title = seasonDetailsModel.seasonName,
                imagesCount = seasonDetailsModel.seasonImages.size,
                watchProgress = seasonDetailsModel.watchProgress,
                isLoading = isLoading,
                onAction = onAction,
                listState = listState,
            )
        }

        item {
            BodyContent(
                seasonDetailsModel = seasonDetailsModel,
                onAction = onAction,
            )
        }

        item { Spacer(modifier = Modifier.height(54.dp)) }
    }

    when (seasonDetailsModel.dialogState) {
        is SeasonDialogState.UnwatchSeasonConfirmation -> {
            SeasonsWatchDialog(
                isWatched = true,
                onAction = onAction,
            )
        }
        is SeasonDialogState.MarkPreviousEpisodesConfirmation -> {
            MarkPreviousEpisodesDialog(
                onMarkAll = { onAction(ConfirmDialogAction) },
                onMarkJustThis = { onAction(SecondaryDialogAction) },
                onDismiss = { onAction(DismissDialog) },
            )
        }
        is SeasonDialogState.UnwatchEpisodeConfirmation -> {
            MarkEpisodeUnwatchedDialog(
                onConfirm = { onAction(ConfirmDialogAction) },
                onDismiss = { onAction(DismissDialog) },
            )
        }
        is SeasonDialogState.MarkPreviousSeasonsConfirmation -> {
            MarkPreviousSeasonsDialog(
                onMarkAll = { onAction(ConfirmDialogAction) },
                onMarkJustThis = { onAction(SecondaryDialogAction) },
                onDismiss = { onAction(DismissDialog) },
            )
        }
        SeasonDialogState.Hidden,
        SeasonDialogState.Gallery,
        -> { }
    }
}

@Composable
internal fun ImageGalleryContent(
    imageList: ImmutableList<SeasonImagesModel>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        flingBehavior = rememberSnapperFlingBehavior(listState),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        items(imageList) { item ->
            PosterCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                imageUrl = item.imageUrl,
                title = "",
                onClick = {},
            )
        }
    }
}

@Composable
private fun HeaderContent(
    scrollState: ScrollState,
    imageUrl: String?,
    title: String,
    watchProgress: Float,
    imagesCount: Int,
    isLoading: Boolean,
    listState: LazyListState,
    onAction: (SeasonDetailsAction) -> Unit,
) {
    val offset = (scrollState.value / 2)
    val offsetDp = with(LocalDensity.current) { offset.toDp() }
    val resources = LocalContext.current.resources

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
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
        contentAlignment = Alignment.BottomCenter,
    ) {
        PosterCard(
            imageUrl = imageUrl,
            title = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = offsetDp),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(contentBackgroundGradient()),
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .clickable { onAction(ShowGallery) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoLibrary,
                contentDescription = cd_navigate_back.resolve(LocalContext.current),
                tint = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = resources.getQuantityString(
                    season_images_count.resourceId,
                    imagesCount,
                    imagesCount,
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }

        AnimatedVisibility(
            visible = isLoading,
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
                    .size(28.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ShowLinearProgressIndicator(
            progress = watchProgress,
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun BodyContent(
    seasonDetailsModel: SeasonDetailsModel,
    onAction: (SeasonDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = title_season_overview.resolve(LocalContext.current),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            ),
        )

        ExpandingText(
            text = seasonDetailsModel.seasonOverview,
            textStyle = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CollapsableContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            episodesCount = seasonDetailsModel.episodeCount,
            watchProgress = seasonDetailsModel.watchProgress,
            isSeasonWatched = seasonDetailsModel.isSeasonWatched,
            episodeDetailsModelList = seasonDetailsModel.episodeDetailsList,
            collapsed = seasonDetailsModel.expandEpisodeItems,
            onAction = onAction,
        )

        CastContent(seasonDetailsModel.seasonCast)
    }
}

@Composable
private fun CastContent(
    castList: ImmutableList<Cast>,
) {
    if (castList.isEmpty()) return
    Column {
        Text(
            text = title_casts.resolve(LocalContext.current),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            ),
        )

        Box(
            contentAlignment = Alignment.BottomCenter,
        ) {
            val lazyListState = rememberLazyListState()

            LazyRow(
                modifier = Modifier,
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(castList) { index, cast ->
                    CastCard(
                        profileUrl = cast.profileUrl,
                        name = cast.name,
                        characterName = cast.characterName,
                        modifier = Modifier.padding(
                            start = if (index == 0) 16.dp else 0.dp,
                            end = if (index == castList.size - 1) 16.dp else 8.dp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SeasonsWatchDialog(
    isWatched: Boolean,
    onAction: (SeasonDetailsAction) -> Unit,
) {
    val context = LocalContext.current

    val title = if (isWatched) {
        dialog_title_unwatched.resolve(context)
    } else {
        dialog_title_watched.resolve(context)
    }

    val message = if (isWatched) {
        dialog_message_unwatched.resolve(context)
    } else {
        dialog_message_watched.resolve(context)
    }

    TvManiacAlertDialog(
        title = title,
        message = message,
        confirmButtonText = dialog_button_yes.resolve(context),
        dismissButtonText = dialog_button_no.resolve(context),
        onConfirm = { onAction(ConfirmDialogAction) },
        onDismiss = { onAction(DismissDialog) },
    )
}

@Composable
private fun MarkPreviousEpisodesDialog(
    onMarkAll: () -> Unit,
    onMarkJustThis: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    TvManiacAlertDialog(
        title = dialog_title_mark_previous.resolve(context),
        message = dialog_message_mark_previous.resolve(context),
        confirmButtonText = dialog_button_mark_all.resolve(context),
        dismissButtonText = dialog_button_just_this.resolve(context),
        onConfirm = onMarkAll,
        onDismiss = onMarkJustThis,
    )
}

@Composable
private fun MarkEpisodeUnwatchedDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    TvManiacAlertDialog(
        title = dialog_title_episode_unwatched.resolve(context),
        message = dialog_message_episode_unwatched.resolve(context),
        confirmButtonText = dialog_button_yes.resolve(context),
        dismissButtonText = dialog_button_no.resolve(context),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Composable
private fun MarkPreviousSeasonsDialog(
    onMarkAll: () -> Unit,
    onMarkJustThis: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    TvManiacAlertDialog(
        title = dialog_title_mark_previous_seasons.resolve(context),
        message = dialog_message_mark_previous_seasons.resolve(context),
        confirmButtonText = dialog_button_mark_all_seasons.resolve(context),
        dismissButtonText = dialog_button_just_this_season.resolve(context),
        onConfirm = onMarkAll,
        onDismiss = onMarkJustThis,
    )
}

@ThemePreviews
@Composable
private fun SeasonDetailScreenPreview(
    @PreviewParameter(SeasonPreviewParameterProvider::class) state: SeasonDetailsModel,
) {
    TvManiacTheme {
        Surface {
            SeasonDetailsScreen(
                state = state,
                snackBarHostState = remember { SnackbarHostState() },
                onAction = {},
            )
        }
    }
}
