package com.thomaskioko.tvmaniac.continuewatching.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ShowLinearProgressIndicator
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingAction
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingMessageShown
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingShowClicked
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingState
import com.thomaskioko.tvmaniac.continuewatching.presenter.MarkUpNextEpisodeWatched
import com.thomaskioko.tvmaniac.continuewatching.presenter.RefreshContinueWatching
import com.thomaskioko.tvmaniac.continuewatching.presenter.ShowTitleClicked
import com.thomaskioko.tvmaniac.continuewatching.presenter.UpNextEpisodeClicked
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.ContinueWatchingItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.i18n.MR.strings.badge_new
import com.thomaskioko.tvmaniac.i18n.MR.strings.badge_premiere
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_up_next
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_up_to_date
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_watchlist_empty_result
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_not_watched_for_while
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.testtags.myshows.MyShowsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun ContinueWatchingScreen(
    state: ContinueWatchingState,
    modifier: Modifier = Modifier,
    onAction: (ContinueWatchingAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(MyShowsTestTags.SCREEN_TEST_TAG),
    ) {
        PullToRefreshBox(
            modifier = Modifier.padding(horizontal = 8.dp),
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(RefreshContinueWatching(forceRefresh = true)) },
        ) {
            AnimatedContent(
                targetState = state.isGridMode,
                transitionSpec = {
                    (scaleIn(animationSpec = spring()) + fadeIn()) togetherWith
                        (scaleOut(animationSpec = spring()) + fadeOut())
                },
                label = "list_style_animation",
            ) { isGridMode ->
                val hasNoItems = state.watchNextItems.isEmpty() && state.staleItems.isEmpty()
                val hasNoEpisodes = state.watchNextEpisodes.isEmpty() && state.staleEpisodes.isEmpty()

                when {
                    state.showLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            LoadingIndicator()
                        }
                    }

                    isGridMode -> {
                        if (hasNoItems) {
                            val message = if (state.query.isNotBlank()) {
                                label_watchlist_empty_result.resolve(context).format(state.query)
                            } else {
                                null
                            }
                            EmptyStateView(
                                modifier = Modifier.testTag(MyShowsTestTags.EMPTY_STATE_TEST_TAG),
                                imageVector = Icons.Outlined.Inbox,
                                title = state.emptyStateText,
                                message = message,
                            )
                        } else {
                            SectionedContinueWatchingGridContent(
                                watchNextTitle = label_discover_up_next.resolve(context),
                                staleTitle = title_not_watched_for_while.resolve(context),
                                watchNextItems = state.watchNextItems,
                                staleItems = state.staleItems,
                                scrollBehavior = scrollBehavior,
                                onItemClicked = { onAction(ContinueWatchingShowClicked(it)) },
                            )
                        }
                    }

                    else -> {
                        if (hasNoEpisodes) {
                            EmptyStateView(
                                imageVector = Icons.Outlined.CheckCircle,
                                title = label_up_to_date.resolve(context),
                            )
                        } else {
                            SectionedUpNextListContent(
                                watchNextTitle = label_discover_up_next.resolve(context),
                                staleTitle = title_not_watched_for_while.resolve(context),
                                premiereLabel = badge_premiere.resolve(context),
                                newLabel = badge_new.resolve(context),
                                watchNextEpisodes = state.watchNextEpisodes,
                                staleEpisodes = state.staleEpisodes,
                                updatingEpisodeIds = state.updatingEpisodeIds,
                                scrollBehavior = scrollBehavior,
                                onEpisodeClicked = { showId, episodeId ->
                                    onAction(UpNextEpisodeClicked(showId, episodeId))
                                },
                                onShowTitleClicked = { showId ->
                                    onAction(ShowTitleClicked(showId))
                                },
                                onMarkWatched = { episode ->
                                    onAction(
                                        MarkUpNextEpisodeWatched(
                                            showTraktId = episode.showTraktId,
                                            episodeId = episode.episodeId,
                                            seasonNumber = episode.seasonNumber,
                                            episodeNumber = episode.episodeNumber,
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }

        TvManiacSnackBarHost(
            message = state.message?.message,
            style = SnackBarStyle.Error,
            onDismiss = { state.message?.let { onAction(ContinueWatchingMessageShown(it.id)) } },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SectionedContinueWatchingGridContent(
    watchNextTitle: String,
    staleTitle: String,
    watchNextItems: ImmutableList<ContinueWatchingItem>,
    staleItems: ImmutableList<ContinueWatchingItem>,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClicked: (Long) -> Unit,
) {
    val chunkedWatchNext = remember(watchNextItems) {
        watchNextItems.chunked(3).map { it.toImmutableList() }.toImmutableList()
    }
    val chunkedStale = remember(staleItems) {
        staleItems.chunked(3).map { it.toImmutableList() }.toImmutableList()
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .testTag(MyShowsTestTags.MY_SHOWS_GRID_TEST_TAG)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(horizontal = 4.dp),
    ) {
        if (chunkedWatchNext.isNotEmpty()) {
            stickyHeader(key = "grid_header_watch_next") {
                SectionHeader(title = watchNextTitle)
            }
            items(
                items = chunkedWatchNext,
                key = { "watchnext_row_${it.first().traktId}" },
                contentType = { "WatchnextRow" },
            ) { rowItems ->
                GridRow(
                    items = rowItems,
                    onItemClicked = onItemClicked,
                )
            }
        }

        if (chunkedStale.isNotEmpty()) {
            stickyHeader(key = "grid_header_stale") {
                SectionHeader(title = staleTitle)
            }
            items(
                items = chunkedStale,
                key = { "stale_row_${it.first().traktId}" },
                contentType = { "StaleRow" },
            ) { rowItems ->
                GridRow(
                    items = rowItems,
                    onItemClicked = onItemClicked,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun GridRow(
    items: ImmutableList<ContinueWatchingItem>,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items.forEach { show ->
            ContinueWatchingGridItem(
                show = show,
                onItemClicked = onItemClicked,
                modifier = Modifier.weight(1f),
            )
        }
        repeat(3 - items.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ContinueWatchingGridItem(
    show: ContinueWatchingItem,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        PosterCard(
            imageUrl = show.posterImageUrl,
            onClick = { onItemClicked(show.traktId) },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .testTag(MyShowsTestTags.showCard(show.traktId)),
            title = show.title.orEmpty(),
            shape = RectangleShape,
        )
        ShowLinearProgressIndicator(
            progress = show.watchProgress,
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SectionedUpNextListContent(
    watchNextTitle: String,
    staleTitle: String,
    premiereLabel: String,
    newLabel: String,
    watchNextEpisodes: ImmutableList<UpNextEpisodeItem>,
    staleEpisodes: ImmutableList<UpNextEpisodeItem>,
    updatingEpisodeIds: ImmutableSet<Long>,
    scrollBehavior: TopAppBarScrollBehavior,
    onEpisodeClicked: (Long, Long) -> Unit,
    onShowTitleClicked: (Long) -> Unit,
    onMarkWatched: (UpNextEpisodeItem) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .testTag(MyShowsTestTags.MY_SHOWS_LIST_TEST_TAG)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        if (watchNextEpisodes.isNotEmpty()) {
            stickyHeader(key = "header_watch_next") {
                SectionHeader(
                    title = watchNextTitle,
                    modifier = Modifier.animateItem(),
                )
            }
            items(
                items = watchNextEpisodes,
                key = { "watchnext_${it.showTraktId}_${it.episodeId}" },
                contentType = { "WatchnextEpisode" },
            ) { episode ->
                ContinueWatchingUpNextListItem(
                    item = episode,
                    premiereLabel = premiereLabel,
                    newLabel = newLabel,
                    onItemClicked = onEpisodeClicked,
                    onShowTitleClicked = { onShowTitleClicked(episode.showTraktId) },
                    onMarkWatched = { onMarkWatched(episode) },
                    modifier = Modifier.animateItem(),
                    isUpdating = episode.episodeId in updatingEpisodeIds,
                )
            }
        }

        if (staleEpisodes.isNotEmpty()) {
            stickyHeader(key = "header_stale") {
                SectionHeader(
                    title = staleTitle,
                    modifier = Modifier.animateItem(),
                )
            }
            items(
                items = staleEpisodes,
                key = { "stale_${it.showTraktId}_${it.episodeId}" },
                contentType = { "StaleEpisode" },
            ) { episode ->
                ContinueWatchingUpNextListItem(
                    item = episode,
                    premiereLabel = premiereLabel,
                    newLabel = newLabel,
                    onItemClicked = onEpisodeClicked,
                    onShowTitleClicked = { onShowTitleClicked(episode.showTraktId) },
                    onMarkWatched = { onMarkWatched(episode) },
                    modifier = Modifier.animateItem(),
                    isUpdating = episode.episodeId in updatingEpisodeIds,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        ) {
            Text(
                text = title.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ContinueWatchingScreenPreview(
    @PreviewParameter(ContinueWatchingPreviewParameterProvider::class) state: ContinueWatchingState,
) {
    ContinueWatchingScreen(
        state = state,
        onAction = {},
    )
}
