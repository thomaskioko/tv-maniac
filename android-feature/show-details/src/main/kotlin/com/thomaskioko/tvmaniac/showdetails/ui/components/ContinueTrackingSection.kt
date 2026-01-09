package com.thomaskioko.tvmaniac.showdetails.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.extensions.calculateScrollOffset
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_continue_tracking
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ContinueTrackingSection(
    episodes: ImmutableList<ContinueTrackingEpisodeModel>,
    scrollIndex: Int,
    onMarkWatched: (ContinueTrackingEpisodeModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = episodes.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title_continue_tracking.resolve(LocalContext.current),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            val lazyListState = rememberLazyListState()
            val scrollOffsetPx = calculateScrollOffset(
                itemWidth = CARD_WIDTH_DP,
                itemSpacing = CARD_SPACING_DP,
                visibleFraction = PREVIOUS_ITEM_VISIBLE_FRACTION,
            )

            LaunchedEffect(scrollIndex) {
                if (scrollIndex > 0 && scrollIndex < episodes.size) {
                    lazyListState.animateScrollToItem(
                        index = scrollIndex - 1,
                        scrollOffset = scrollOffsetPx,
                    )
                }
            }

            LazyRow(
                state = lazyListState,
                modifier = Modifier.fillMaxWidth(),
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(
                    items = episodes,
                    key = { _, episode -> episode.episodeId },
                ) { _, episode ->
                    ContinueTrackingCard(
                        episode = episode,
                        onMarkWatched = { onMarkWatched(episode) },
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ContinueTrackingSectionPreview() {
    TvManiacTheme {
        Surface {
            ContinueTrackingSection(
                episodes = persistentListOf(
                    ContinueTrackingEpisodeModel(
                        episodeId = 1L,
                        seasonId = 1L,
                        showId = 1L,
                        episodeNumber = 1,
                        seasonNumber = 2,
                        episodeNumberFormatted = "S02 | E01",
                        episodeTitle = "First Episode",
                        imageUrl = "/still1.jpg",
                        isWatched = true,
                        daysUntilAir = null,
                        hasAired = true,
                    ),
                    ContinueTrackingEpisodeModel(
                        episodeId = 2L,
                        seasonId = 1L,
                        showId = 1L,
                        episodeNumber = 2,
                        seasonNumber = 2,
                        episodeNumberFormatted = "S02 | E02",
                        episodeTitle = "Second Episode",
                        imageUrl = null,
                        isWatched = false,
                        daysUntilAir = null,
                        hasAired = true,
                    ),
                    ContinueTrackingEpisodeModel(
                        episodeId = 3L,
                        seasonId = 1L,
                        showId = 1L,
                        episodeNumber = 3,
                        seasonNumber = 2,
                        episodeNumberFormatted = "S02 | E03",
                        episodeTitle = "Upcoming Episode",
                        imageUrl = null,
                        isWatched = false,
                        daysUntilAir = 7,
                        hasAired = false,
                    ),
                ),
                scrollIndex = 1,
                onMarkWatched = {},
            )
        }
    }
}

private val CARD_WIDTH_DP = 300.dp
private val CARD_SPACING_DP = 12.dp
private const val PREVIOUS_ITEM_VISIBLE_FRACTION = 0.1f
