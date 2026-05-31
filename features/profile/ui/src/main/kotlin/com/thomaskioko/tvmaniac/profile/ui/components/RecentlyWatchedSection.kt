package com.thomaskioko.tvmaniac.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.InlineSectionError
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileRecentItem
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.testtags.component.CollapsibleSectionTestTags
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun RecentlyWatchedSection(
    recentlyWatched: SectionState<ProfileRecentItem>,
    title: String,
    retryLabel: String,
    onShowClick: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (recentlyWatched is SectionState.Empty) return

    CollapsibleSection(
        title = title,
        modifier = modifier,
        toggleTestTag = CollapsibleSectionTestTags.toggle(ProfileTestTags.RECENTLY_WATCHED_SECTION_KEY),
    ) {
        val posterWidth = ImageType.Poster.width

        when (recentlyWatched) {
            SectionState.Loading -> SkeletonRow(posterWidth = posterWidth)
            is SectionState.Error -> InlineSectionError(
                message = recentlyWatched.message.message,
                retryLabel = retryLabel,
                onRetry = onRetry,
                retryModifier = Modifier.testTag(ProfileTestTags.RECENTLY_WATCHED_RETRY_TEST_TAG),
            )
            is SectionState.Content -> EpisodeRow(
                items = recentlyWatched.items,
                posterWidth = posterWidth,
                onShowClick = onShowClick,
            )
            SectionState.Empty -> Unit
        }
    }
}

@Composable
private fun EpisodeRow(
    items: ImmutableList<ProfileRecentItem>,
    posterWidth: Dp,
    onShowClick: (Long) -> Unit,
) {
    LazyRow(
        modifier = Modifier.testTag(ProfileTestTags.RECENTLY_WATCHED_ROW_TEST_TAG),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = items,
            key = { "${it.traktId}-${it.episodeLabel}" },
        ) { item ->
            EpisodeCard(item = item, posterWidth = posterWidth, onShowClick = onShowClick)
        }
    }
}

@Composable
private fun EpisodeCard(
    item: ProfileRecentItem,
    posterWidth: Dp,
    onShowClick: (Long) -> Unit,
) {
    Column(modifier = Modifier.width(posterWidth)) {
        PosterCard(
            imageUrl = item.posterUrl,
            title = item.title,
            imageWidth = posterWidth,
            shape = MaterialTheme.shapes.medium,
            onClick = { onShowClick(item.traktId) },
            modifier = Modifier.testTag(ProfileTestTags.showCard(item.traktId)),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = item.episodeLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SkeletonRow(posterWidth: Dp) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(3) {
            Column(modifier = Modifier.width(posterWidth)) {
                ShimmerBox(
                    modifier = Modifier
                        .width(posterWidth)
                        .aspectRatio(ImageType.Poster.aspect),
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                ShimmerBox(
                    modifier = Modifier
                        .width(posterWidth)
                        .height(14.dp),
                    shape = MaterialTheme.shapes.small,
                )

                Spacer(modifier = Modifier.height(4.dp))

                ShimmerBox(
                    modifier = Modifier
                        .width(posterWidth * 0.5f)
                        .height(12.dp),
                    shape = MaterialTheme.shapes.small,
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RecentlyWatchedSectionPreview() {
    RecentlyWatchedSection(
        recentlyWatched = SectionState.Content(
            persistentListOf(
                ProfileRecentItem(
                    traktId = 1,
                    tmdbId = 1396,
                    title = "Breaking Bad",
                    posterUrl = null,
                    episodeLabel = "S05E14",
                ),
                ProfileRecentItem(
                    traktId = 2,
                    tmdbId = 1399,
                    title = "Game of Thrones",
                    posterUrl = null,
                    episodeLabel = "S08E06",
                ),
            ),
        ),
        title = "Recently Watched",
        retryLabel = "Retry",
        onShowClick = {},
        onRetry = {},
    )
}
