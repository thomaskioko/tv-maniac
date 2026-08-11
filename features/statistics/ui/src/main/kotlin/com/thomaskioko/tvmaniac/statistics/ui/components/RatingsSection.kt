package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.statistics.presenter.model.RatingBar
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val RatingLabelWidth = 20.dp
private val CountLabelMinWidth = 28.dp

@Composable
internal fun RatingsSection(
    ratings: ImmutableList<RatingBar>,
    title: String,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = title,
        modifier = modifier.testTag(StatisticsTestTags.RATINGS_SECTION_TEST_TAG),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        ) {
            ratings.forEach { rating ->
                RatingRow(
                    rating = rating,
                    modifier = Modifier.testTag(StatisticsTestTags.ratingRow(rating.rating)),
                )
            }
        }
    }
}

@Composable
private fun RatingRow(
    rating: RatingBar,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
    ) {
        Text(
            text = rating.rating.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(RatingLabelWidth),
        )

        StatisticBar(
            fraction = rating.fraction,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = rating.count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = CountLabelMinWidth),
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingsSectionPreview() {
    RatingsSection(
        ratings = persistentListOf(
            RatingBar(rating = 10, count = 12, fraction = 1f),
            RatingBar(rating = 9, count = 8, fraction = 0.66f),
            RatingBar(rating = 8, count = 10, fraction = 0.83f),
            RatingBar(rating = 7, count = 3, fraction = 0.25f),
            RatingBar(rating = 6, count = 1, fraction = 0.08f),
        ),
        title = "Your ratings",
    )
}
