package com.thomaskioko.tvmaniac.seasondetails.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CastCard
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.HorizontalOutlinedButton
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.SheetDragHandle
import com.thomaskioko.tvmaniac.compose.components.ShowLinearProgressIndicator
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacAlertDialog
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.actionIconWhen
import com.thomaskioko.tvmaniac.compose.components.rememberShowAppBarBackground
import com.thomaskioko.tvmaniac.compose.extensions.contentBackgroundGradient
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.ImageDimens
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.base.ActivityScope
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
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_action_rate
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_casts
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_season_overview
import com.thomaskioko.tvmaniac.i18n.MR.strings.unexpected_error_retry
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.seasondetails.presenter.ConfirmDialogAction
import com.thomaskioko.tvmaniac.seasondetails.presenter.DismissDialog
import com.thomaskioko.tvmaniac.seasondetails.presenter.EpisodeClicked
import com.thomaskioko.tvmaniac.seasondetails.presenter.ReloadSeasonDetails
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsAction
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsBackClicked
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsMessageShown
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDialogState
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonRatingClicked
import com.thomaskioko.tvmaniac.seasondetails.presenter.SecondaryDialogAction
import com.thomaskioko.tvmaniac.seasondetails.presenter.ShowGallery
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.Cast
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonImagesModel
import com.thomaskioko.tvmaniac.seasondetails.ui.components.CollapsableContent
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi
import kotlinx.collections.immutable.ImmutableList

@ScreenUi(presenter = SeasonDetailsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun SeasonDetailsScreen(
    presenter: SeasonDetailsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    SeasonDetailsScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
        onEpisodeLongPress = { presenter.dispatch(EpisodeClicked(it.id)) },
    )
}

@Composable
internal fun SeasonDetailsScreen(
    state: SeasonDetailsModel,
    modifier: Modifier = Modifier,
    onAction: (SeasonDetailsAction) -> Unit,
    onEpisodeLongPress: (EpisodeDetailsModel) -> Unit = {},
) {
    val listState = rememberLazyListState()

    TvManiacBottomSheetScaffold(
        modifier = modifier.testTag(SeasonDetailsTestTags.SCREEN_TEST_TAG),
        showBottomSheet = state.isGalleryVisible,
        sheetContent = { ImageGalleryContent(imageList = state.seasonImages) },
        onDismissBottomSheet = { onAction(DismissDialog) },
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
                    EmptyStateView(
                        imageVector = Icons.Outlined.ErrorOutline,
                        title = state.message?.message ?: unexpected_error_retry.resolve(LocalContext.current),
                        buttonText = generic_retry.resolve(LocalContext.current),
                        onClick = { onAction(ReloadSeasonDetails) },
                    )
                } else {
                    LazyColumnContent(
                        seasonDetailsModel = state,
                        isLoading = state.isRefreshing,
                        contentPadding = contentPadding,
                        onAction = onAction,
                        onEpisodeLongPress = onEpisodeLongPress,
                        listState = listState,
                    )
                }

                var appBarHeight by remember { mutableIntStateOf(0) }
                val showPinnedProgress by rememberShowAppBarBackground(listState) { appBarHeight }

                Column {
                    RefreshCollapsableTopAppBar(
                        modifier = Modifier.onSizeChanged { appBarHeight = it.height },
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
                        navIconModifier = Modifier.testTag(SeasonDetailsTestTags.BACK_BUTTON_TEST_TAG),
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

                    AnimatedVisibility(
                        visible = showPinnedProgress,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        ShowLinearProgressIndicator(
                            progress = state.watchProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                        )
                    }
                }

                TvManiacSnackBarHost(
                    message = if (!state.showError) state.message?.message else null,
                    style = SnackBarStyle.Error,
                    onDismiss = { state.message?.let { onAction(SeasonDetailsMessageShown(it.id)) } },
                )
            }
        },
    )
}

@Composable
internal fun LazyColumnContent(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    isLoading: Boolean,
    seasonDetailsModel: SeasonDetailsModel,
    contentPadding: PaddingValues,
    onAction: (SeasonDetailsAction) -> Unit,
    onEpisodeLongPress: (EpisodeDetailsModel) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .testTag(SeasonDetailsTestTags.SEASON_DETAILS_LIST_TEST_TAG),
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
    ) {
        item(key = "header") {
            HeaderContent(
                imageUrl = seasonDetailsModel.imageUrl,
                title = seasonDetailsModel.seasonName,
                imagesCount = seasonDetailsModel.seasonImages.size,
                watchProgress = seasonDetailsModel.watchProgress,
                userRating = seasonDetailsModel.userRating,
                isLoading = isLoading,
                onAction = onAction,
            )
        }

        item(key = "body") {
            BodyContent(
                seasonDetailsModel = seasonDetailsModel,
                onAction = onAction,
                onEpisodeLongPress = onEpisodeLongPress,
            )
        }

        item(key = "footer") { Spacer(modifier = Modifier.height(TvManiacSpacing.xxLarge)) }
    }

    when (seasonDetailsModel.dialogState) {
        is SeasonDialogState.WatchSeasonConfirmation -> {
            SeasonsWatchDialog(
                isWatched = false,
                onAction = onAction,
            )
        }
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
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = ImageDimens.GridItemSpacing,
        horizontalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
        modifier = modifier.fillMaxSize(),
    ) {
        items(
            items = imageList,
            key = { it.id },
            contentType = { "SeasonImage" },
        ) { item ->
            PosterCard(
                imageUrl = item.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                title = "",
            )
        }
    }
}

@Composable
private fun HeaderContent(
    imageUrl: String?,
    title: String,
    watchProgress: Float,
    imagesCount: Int,
    userRating: Int?,
    isLoading: Boolean,
    onAction: (SeasonDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalResources.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .clipToBounds(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        PosterCard(
            imageUrl = imageUrl,
            title = title,
            modifier = Modifier.fillMaxWidth(),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(contentBackgroundGradient()),
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.xLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        ) {
            HorizontalOutlinedButton(
                text = resources.getQuantityString(
                    season_images_count.resourceId,
                    imagesCount,
                    imagesCount,
                ),
                onClick = { onAction(ShowGallery) },
                shape = CircleShape,
                borderColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = cd_navigate_back.resolve(LocalContext.current),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                },
            )

            HorizontalOutlinedButton(
                text = label_action_rate.resolve(LocalContext.current),
                onClick = { onAction(SeasonRatingClicked) },
                modifier = Modifier.testTag(SeasonDetailsTestTags.RATE_BUTTON_TEST_TAG),
                shape = CircleShape,
                borderColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                leadingIcon = {
                    Icon(
                        imageVector = if (userRating != null) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                },
            )
        }

        AnimatedVisibility(
            visible = isLoading,
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(TvManiacSpacing.xLarge)
                    .size(28.dp),
            )
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

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
    onEpisodeLongPress: (EpisodeDetailsModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = title_season_overview.resolve(LocalContext.current),
            modifier = Modifier
                .fillMaxWidth()
                .padding(TvManiacSpacing.medium),
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
                .padding(horizontal = TvManiacSpacing.medium),
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.xSmall))

        CollapsableContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            episodesCount = seasonDetailsModel.episodeCount,
            watchProgress = seasonDetailsModel.watchProgress,
            isSeasonWatched = seasonDetailsModel.isSeasonWatched,
            episodeDetailsModelList = seasonDetailsModel.episodeDetailsList,
            collapsed = seasonDetailsModel.expandEpisodeItems,
            onAction = onAction,
            onEpisodeLongPress = onEpisodeLongPress,
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
                .padding(TvManiacSpacing.medium)
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
                flingBehavior = rememberSnapFlingBehavior(lazyListState, SnapPosition.Start),
            ) {
                itemsIndexed(castList) { index, cast ->
                    CastCard(
                        profileUrl = cast.profileUrl,
                        name = cast.name,
                        characterName = cast.characterName,
                        modifier = Modifier.padding(
                            start = if (index == 0) TvManiacSpacing.medium else TvManiacSpacing.none,
                            end = if (index == castList.size - 1) TvManiacSpacing.medium else TvManiacSpacing.xSmall,
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
        confirmButtonTestTag = if (isWatched) {
            SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG
        } else {
            SeasonDetailsTestTags.WATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG
        },
        dismissButtonTestTag = if (isWatched) {
            SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_DISMISS_BUTTON_TEST_TAG
        } else {
            SeasonDetailsTestTags.WATCH_SEASON_DIALOG_DISMISS_BUTTON_TEST_TAG
        },
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
        confirmButtonTestTag = SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG,
        dismissButtonTestTag = SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_DISMISS_BUTTON_TEST_TAG,
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
        confirmButtonTestTag = SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG,
        dismissButtonTestTag = SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_DISMISS_BUTTON_TEST_TAG,
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
        confirmButtonTestTag = SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG,
        dismissButtonTestTag = SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_DISMISS_BUTTON_TEST_TAG,
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SeasonDetailScreenPreview(
    @PreviewParameter(SeasonPreviewParameterProvider::class) state: SeasonDetailsModel,
) {
    SeasonDetailsScreen(
        state = state,
        onAction = {},
    )
}
