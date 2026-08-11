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
import com.thomaskioko.tvmaniac.statistics.presenter.model.ShowRowItem
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ShowRowSection(
    shows: ImmutableList<ShowRowItem>,
    title: String,
    rowTestTag: String,
    cardTestTag: (Long) -> String,
    onShowClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = title,
        modifier = modifier,
    ) {
        LazyRow(
            modifier = Modifier.testTag(rowTestTag),
            contentPadding = PaddingValues(horizontal = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
        ) {
            items(
                items = shows,
                key = { it.showId },
                contentType = { "show_row" },
            ) { show ->
                ShowRowCard(
                    show = show,
                    onClick = { onShowClick(show.showId) },
                    modifier = Modifier.testTag(cardTestTag(show.showId)),
                )
            }
        }
    }
}

@Composable
private fun ShowRowCard(
    show: ShowRowItem,
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
private fun ShowRowSectionPreview() {
    ShowRowSection(
        shows = persistentListOf(
            ShowRowItem(
                showId = 1396,
                title = "Breaking Bad",
                posterPath = null,
                caption = "62 episodes",
            ),
            ShowRowItem(
                showId = 1399,
                title = "Game of Thrones",
                posterPath = null,
                caption = "73 episodes",
            ),
        ),
        title = "Episodes by show",
        rowTestTag = StatisticsTestTags.MOST_WATCHED_ROW_TEST_TAG,
        cardTestTag = StatisticsTestTags::mostWatchedShowCard,
        onShowClick = {},
    )
}
