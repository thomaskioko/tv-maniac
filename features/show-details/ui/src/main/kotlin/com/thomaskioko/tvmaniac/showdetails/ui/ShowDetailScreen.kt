package com.thomaskioko.tvmaniac.showdetails.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.CastCard
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.FilledTextButton
import com.thomaskioko.tvmaniac.compose.components.FilledVerticalIconButton
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacAlertDialog
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.actionIconWhen
import com.thomaskioko.tvmaniac.compose.extensions.backgroundGradient
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.i18n.MR.strings.btn_add_to_list
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_navigate_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.following
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_error_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_retry
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_casts
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_providers
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_providers_label
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_similar
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_trailer
import com.thomaskioko.tvmaniac.i18n.MR.strings.unfollow
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.CreateListSubmitted
import com.thomaskioko.tvmaniac.presenter.showdetails.DetailBackClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.DetailShowClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.DismissLoginPrompt
import com.thomaskioko.tvmaniac.presenter.showdetails.DismissShowsListSheet
import com.thomaskioko.tvmaniac.presenter.showdetails.FollowShowClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.LoginClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.MarkEpisodeUnwatched
import com.thomaskioko.tvmaniac.presenter.showdetails.MarkEpisodeWatched
import com.thomaskioko.tvmaniac.presenter.showdetails.ReloadShowDetails
import com.thomaskioko.tvmaniac.presenter.showdetails.SeasonClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowCreateListField
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsContent
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsMessageShown
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowShowsListSheet
import com.thomaskioko.tvmaniac.presenter.showdetails.ToggleShowInList
import com.thomaskioko.tvmaniac.presenter.showdetails.UpdateCreateListName
import com.thomaskioko.tvmaniac.presenter.showdetails.WatchTrailerClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.model.CastModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.showdetails.ui.components.ContinueTrackingSection
import com.thomaskioko.tvmaniac.showdetails.ui.components.WatchProgressSection
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

@Composable
public fun ShowDetailsScreen(
    presenter: ShowDetailsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    val listState = rememberLazyListState()

    ShowDetailsScreen(
        modifier = modifier,
        state = state,
        title = state.showDetails.title,
        listState = listState,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun ShowDetailsScreen(
    state: ShowDetailsContent,
    title: String,
    listState: LazyListState,
    onAction: (ShowDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    TvManiacBottomSheetScaffold(
        modifier = modifier,
        showBottomSheet = state.showListSheet,
        onDismissBottomSheet = { onAction(DismissShowsListSheet) },
        sheetDragHandle = {
            ListSheetTopBar(
                title = state.sheetTitle,
                showCreateField = state.showCreateListField,
                onClose = { onAction(DismissShowsListSheet) },
                onCreateClicked = { onAction(ShowCreateListField) },
            )
        },
        sheetContent = {
            ShowListSheetContent(state, onAction)
        },
        content = { contentPadding ->
            Box(Modifier.fillMaxSize()) {
                LazyColumnContent(
                    detailsContent = state,
                    contentPadding = contentPadding,
                    listState = listState,
                    onAction = onAction,
                )

                RefreshCollapsableTopAppBar(
                    listState = listState,
                    title = {
                        Text(
                            text = title,
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
                    actionIcon = actionIconWhen(state.message == null) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    isRefreshing = state.isRefreshing,
                    onNavIconClicked = { onAction(DetailBackClicked) },
                    onActionIconClicked = { onAction(ReloadShowDetails) },
                )

                TvManiacSnackBarHost(
                    message = state.message?.message,
                    style = SnackBarStyle.Error,
                    onDismiss = { state.message?.let { onAction(ShowDetailsMessageShown(it.id)) } },
                )
            }
        },
    )

    if (state.showLoginPrompt) {
        TvManiacAlertDialog(
            title = state.loginRequiredTitle,
            message = state.loginRequiredMessage,
            confirmButtonText = state.loginRequiredConfirmText,
            onConfirm = { onAction(LoginClicked) },
            onDismiss = { onAction(DismissLoginPrompt) },
        )
    }
}

@Composable
internal fun ShowListSheetContent(
    state: ShowDetailsContent,
    onAction: (ShowDetailsAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        val title = state.showDetails.title

        Card(
            modifier = Modifier
                .size(width = 150.dp, height = 240.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp,
            ),
        ) {
            AsyncImageComposable(
                model = state.showDetails.posterImageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = state.listsHeaderText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.traktLists.isEmpty()) {
            EmptyListContent(state)
        } else {
            TraktListItems(state, onAction)
        }

        Spacer(modifier = Modifier.height(16.dp))

        CreateListInlineField(state, onAction)
    }
}

@Composable
private fun TraktListItems(
    state: ShowDetailsContent,
    onAction: (ShowDetailsAction) -> Unit,
) {
    state.traktLists.forEach { list ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = list.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = list.showCountText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                androidx.compose.material3.Switch(
                    checked = list.isShowInList,
                    onCheckedChange = {
                        onAction(ToggleShowInList(listId = list.id, isCurrentlyInList = list.isShowInList))
                    },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                )
            }
        }
    }
}

@Composable
private fun EmptyListContent(
    state: ShowDetailsContent,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = state.emptyListText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ListSheetTopBar(
    title: String,
    showCreateField: Boolean,
    onClose: () -> Unit,
    onCreateClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.FilledIconButton(
            onClick = onClose,
            modifier = Modifier.size(36.dp),
            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (!showCreateField) {
            androidx.compose.material3.FilledIconButton(
                onClick = onCreateClicked,
                modifier = Modifier.size(36.dp),
                colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.LibraryAddCheck,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        } else {
            Spacer(modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun CreateListInlineField(
    state: ShowDetailsContent,
    onAction: (ShowDetailsAction) -> Unit,
) {
    androidx.compose.animation.AnimatedVisibility(visible = state.showCreateListField) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = state.createListName,
                onValueChange = { if (it.length <= 50) onAction(UpdateCreateListName(it)) },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = state.createListPlaceholder,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        ),
                    )
                },
                singleLine = true,
                enabled = !state.isCreatingList,
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                ),
            )

            if (state.isCreatingList) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            } else {
                FilledTextButton(
                    onClick = { onAction(CreateListSubmitted) },
                    enabled = state.createListName.isNotBlank(),
                    buttonColors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(state.createListDoneText)
                }
            }
        }
    }
}

@Composable
internal fun LazyColumnContent(
    detailsContent: ShowDetailsContent,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onAction: (ShowDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
    ) {
        item {
            HeaderContent(
                show = detailsContent.showDetails,
                onUpdateFavoriteClicked = { onAction(FollowShowClicked(it)) },
                onAddToListClicked = { onAction(ShowShowsListSheet) },
            )
        }

        item {
            if (!detailsContent.isRefreshing && detailsContent.showDetails == ShowDetailsModel.Empty && detailsContent.message != null) {
                EmptyStateView(
                    modifier = Modifier.padding(top = 16.dp),
                    imageVector = Icons.Outlined.ErrorOutline,
                    title = generic_error_message.resolve(LocalContext.current),
                    buttonText = generic_retry.resolve(LocalContext.current),
                    onClick = { onAction(ReloadShowDetails) },
                )
            } else {
                ShowInfoContent(
                    showDetails = detailsContent.showDetails,
                    selectedSeasonIndex = detailsContent.selectedSeasonIndex,
                    continueTrackingEpisodes = detailsContent.continueTrackingEpisodes,
                    continueTrackingScrollIndex = detailsContent.continueTrackingScrollIndex,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun ShowInfoContent(
    showDetails: ShowDetailsModel,
    selectedSeasonIndex: Int,
    continueTrackingEpisodes: ImmutableList<ContinueTrackingEpisodeModel>,
    continueTrackingScrollIndex: Int,
    onAction: (ShowDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ContinueTrackingSection(
            episodes = continueTrackingEpisodes,
            scrollIndex = continueTrackingScrollIndex,
            onMarkWatched = { episode ->
                if (episode.isWatched) {
                    onAction(
                        MarkEpisodeUnwatched(
                            showTraktId = episode.showTraktId,
                            episodeId = episode.episodeId,
                        ),
                    )
                } else {
                    onAction(
                        MarkEpisodeWatched(
                            showTraktId = episode.showTraktId,
                            episodeId = episode.episodeId,
                            seasonNumber = episode.seasonNumber,
                            episodeNumber = episode.episodeNumber,
                        ),
                    )
                }
            },
        )

        WatchProgressSection(
            status = showDetails.status,
            watchedEpisodesCount = showDetails.watchedEpisodesCount,
            totalEpisodesCount = showDetails.totalEpisodesCount,
            seasonsList = showDetails.seasonsList,
            selectedSeasonIndex = selectedSeasonIndex,
            showHeader = continueTrackingEpisodes.isEmpty(),
            onSeasonClicked = { index, season ->
                onAction(
                    SeasonClicked(
                        ShowSeasonDetailsParam(
                            season.tvShowId,
                            season.seasonId,
                            season.seasonNumber,
                            selectedSeasonIndex = index,
                        ),
                    ),
                )
            },
        )

        WatchProvider(list = showDetails.providers)

        TrailersContent(
            trailersList = showDetails.trailersList,
            onAction = onAction,
        )

        CastContent(castsList = showDetails.castsList)

        SimilarShowsContent(
            similarShows = showDetails.similarShows,
            onShowClicked = { onAction(DetailShowClicked(it)) },
        )

        Spacer(modifier = Modifier.height(54.dp))
    }
}

@Composable
private fun HeaderContent(
    show: ShowDetailsModel?,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    onAddToListClicked: () -> Unit,
) {
    val density = LocalDensity.current
    val containerHeight = with(density) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val headerHeight = containerHeight / 1.5f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight),
    ) {
        KenBurnsViewImage(
            imageUrl = show?.backdropImageUrl,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
        )

        if (show != null) {
            ShowBody(
                show = show,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onAddToListClicked = onAddToListClicked,
            )
        }
    }
}

@Composable
private fun ShowBody(
    show: ShowDetailsModel,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    onAddToListClicked: () -> Unit,
) {
    val gradient = backgroundGradient()
    val surfaceGradient = remember(gradient) { gradient.reversed() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Brush.verticalGradient(surfaceGradient))
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
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

            ShowMetadata(
                releaseYear = show.year,
                status = show.status,
                seasonNumber = show.seasonsList.size,
                language = show.language,
                rating = show.rating,
            )

            ExpandingText(
                text = show.overview,
                textStyle = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
            )

            Spacer(modifier = Modifier.height(8.dp))

            GenreText(show.genres)

            Spacer(modifier = Modifier.height(8.dp))

            ShowDetailButtons(
                isFollowed = show.isInLibrary,
                onTrackShowClicked = onUpdateFavoriteClicked,
                onAddToList = onAddToListClicked,
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
internal fun ShowMetadata(
    releaseYear: String,
    status: String?,
    seasonNumber: Int,
    language: String?,
    rating: Double,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val resources = LocalContext.current.resources

    val divider = remember(colorScheme.secondary, typography.labelMedium) {
        buildAnnotatedString {
            val tagStyle = typography.labelMedium
                .toSpanStyle()
                .copy(color = colorScheme.secondary)
            withStyle(tagStyle) { append("  •  ") }
        }
    }

    val text = remember(
        status,
        releaseYear,
        seasonNumber,
        language,
        colorScheme.secondary,
        colorScheme.onSurface,
        typography.labelMedium,
    ) {
        buildAnnotatedString {
            val statusStyle = typography.labelMedium
                .toSpanStyle()
                .copy(
                    color = colorScheme.secondary,
                    background = colorScheme.secondary.copy(alpha = 0.08f),
                )

            val tagStyle = typography.labelMedium
                .toSpanStyle()
                .copy(color = colorScheme.onSurface)

            if (!status.isNullOrBlank()) {
                withStyle(statusStyle) {
                    append(" ")
                    append(status)
                    append(" ")
                }
                append(divider)
            }

            withStyle(tagStyle) { append(releaseYear) }

            if (seasonNumber > 0) {
                append(divider)
                withStyle(tagStyle) {
                    append(resources.getQuantityString(MR.plurals.season_count.resourceId, seasonNumber, seasonNumber))
                }
            }

            append(divider)
            language?.let { language ->
                withStyle(tagStyle) { append(language) }
                append(divider)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = "$rating",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreText(
    genreList: ImmutableList<String>,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(genreList) { genre ->
            Spacer(modifier = Modifier.width(4.dp))

            FilledTextButton(
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
internal fun ShowDetailButtons(
    isFollowed: Boolean,
    onTrackShowClicked: (Boolean) -> Unit,
    onAddToList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val context = LocalContext.current
        FilledVerticalIconButton(
            shape = MaterialTheme.shapes.medium,
            text = if (isFollowed) unfollow.resolve(context) else following.resolve(context),
            imageVector = if (isFollowed) Icons.Filled.RemoveCircle else Icons.Filled.AddCircle,
            containerColor = if (isFollowed) MaterialTheme.colorScheme.error.copy(alpha = 0.65f) else MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium,
            onClick = { onTrackShowClicked(isFollowed) },
        )

        FilledVerticalIconButton(
            shape = MaterialTheme.shapes.medium,
            text = btn_add_to_list.resolve(context),
            imageVector = Icons.Outlined.AutoAwesomeMotion,
            containerColor = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium,
            onClick = onAddToList,
        )
    }
}

@Composable
internal fun WatchProvider(
    list: ImmutableList<ProviderModel>,
    modifier: Modifier = Modifier,
) {
    if (list.isEmpty()) return

    Spacer(modifier = Modifier.height(8.dp))

    val context = LocalContext.current

    TextLoadingItem(
        title = title_providers.resolve(context),
        subTitle = title_providers_label.resolve(context),
    ) {
        val lazyListState = rememberLazyListState()

        LazyRow(
            modifier = modifier,
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            itemsIndexed(list) { index, tvShow ->
                val value = if (index == 0) 16 else 4

                Spacer(modifier = Modifier.width(value.dp))

                Card(
                    modifier = Modifier
                        .size(width = 80.dp, height = 60.dp)
                        .padding(
                            end = if (index == list.size - 1) 16.dp else 8.dp,
                        ),
                    shape = MaterialTheme.shapes.small,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp,
                    ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    AsyncImageComposable(
                        model = tvShow.logoUrl,
                        contentDescription = tvShow.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .animateItem(),
                    )
                }
            }
        }
    }
}

@Composable
private fun CastContent(
    castsList: ImmutableList<CastModel>,
) {
    if (castsList.isEmpty()) return

    TextLoadingItem(title = title_casts.resolve(LocalContext.current)) {
        Box(
            contentAlignment = Alignment.BottomCenter,
        ) {
            val lazyListState = rememberLazyListState()

            LazyRow(
                modifier = Modifier,
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(castsList) { index, cast ->
                    CastCard(
                        profileUrl = cast.profileUrl,
                        name = cast.name,
                        characterName = cast.characterName,
                        modifier = Modifier.padding(
                            start = if (index == 0) 16.dp else 0.dp,
                            end = if (index == castsList.size - 1) 16.dp else 8.dp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun TrailersContent(
    trailersList: ImmutableList<TrailerModel>,
    onAction: (ShowDetailsAction) -> Unit,
) {
    if (trailersList.isEmpty()) return

    Spacer(modifier = Modifier.height(16.dp))

    TextLoadingItem(
        title = title_trailer.resolve(LocalContext.current),
    ) {
        val lazyListState = rememberLazyListState()

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            itemsIndexed(trailersList) { index, trailer ->
                val value = if (index == 0) 16 else 8
                Spacer(modifier = Modifier.width(value.dp))

                Column {
                    Card(
                        modifier = Modifier.clickable { onAction(WatchTrailerClicked(trailer.showTmdbId)) },
                        shape = RoundedCornerShape(4.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp,
                        ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp),
                            )
                        }
                    }

                    Text(
                        text = trailer.name,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .widthIn(0.dp, 280.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
internal fun SimilarShowsContent(
    similarShows: ImmutableList<ShowModel>,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit = {},
) {
    if (similarShows.isEmpty()) return

    Spacer(modifier = Modifier.height(16.dp))

    HorizontalRowContent(
        modifier = modifier,
        title = title_similar.resolve(LocalContext.current),
        items = similarShows,
        onShowClicked = onShowClicked,
    )
}

@Composable
private fun HorizontalRowContent(
    modifier: Modifier,
    title: String,
    items: ImmutableList<ShowModel>,
    onShowClicked: (Long) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    TextLoadingItem(title = title) {
        LazyRow(
            modifier = modifier,
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            itemsIndexed(items) { index, tvShow ->
                val value = if (index == 0) 16 else 4

                Spacer(modifier = Modifier.width(value.dp))

                PosterCard(
                    imageUrl = tvShow.posterImageUrl,
                    title = tvShow.title,
                    onClick = { onShowClicked(tvShow.traktId) },
                    imageWidth = 84.dp,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ShowDetailScreenPreview(
    @PreviewParameter(DetailPreviewParameterProvider::class) state: ShowDetailsContent,
) {
    TvManiacTheme {
        Surface {
            ShowDetailsScreen(
                state = state,
                title = "",
                listState = LazyListState(),
                onAction = {},
            )
        }
    }
}
