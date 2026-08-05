package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.statistics.presenter.model.MostWatchedShowItem
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun MostWatchedShowsSection(
    shows: ImmutableList<MostWatchedShowItem>,
    title: String,
    onShowClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = title,
        modifier = modifier,
    ) {
        LazyRow(
            modifier = Modifier.testTag(StatisticsTestTags.MOST_WATCHED_ROW_TEST_TAG),
            contentPadding = PaddingValues(horizontal = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
        ) {
            items(
                items = shows,
                key = { it.showId },
                contentType = { "most_watched_show" },
            ) { show ->
                MostWatchedShowCard(
                    show = show,
                    onClick = { onShowClick(show.showId) },
                    modifier = Modifier.testTag(StatisticsTestTags.mostWatchedShowCard(show.showId)),
                )
            }
        }
    }
}

@Composable
private fun MostWatchedShowCard(
    show: MostWatchedShowItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.width(ImageType.Poster.width)) {
        PosterCard(
            imageUrl = show.posterPath,
            title = show.title,
            onClick = onClick,
            imageWidth = ImageType.Poster.width,
            shape = MaterialTheme.shapes.medium,
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.xxSmall))

        Text(
            text = show.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = show.caption,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun MostWatchedShowsSectionPreview() {
    MostWatchedShowsSection(
        shows = persistentListOf(
            MostWatchedShowItem(
                showId = 1396,
                title = "Breaking Bad",
                posterPath = null,
                episodeCount = 62,
                caption = "62 episodes",
            ),
            MostWatchedShowItem(
                showId = 1399,
                title = "Game of Thrones",
                posterPath = null,
                episodeCount = 73,
                caption = "73 episodes",
            ),
        ),
        title = "Most watched shows",
        onShowClick = {},
    )
}
