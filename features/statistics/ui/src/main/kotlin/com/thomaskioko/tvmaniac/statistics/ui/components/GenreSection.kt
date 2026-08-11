package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.statistics.presenter.model.GenreSlice
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun GenreSection(
    genres: ImmutableList<GenreSlice>,
    title: String,
    emptyMessage: String,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = title,
        modifier = modifier.testTag(StatisticsTestTags.GENRES_SECTION_TEST_TAG),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
        ) {
            if (genres.isEmpty()) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag(StatisticsTestTags.GENRES_EMPTY_MESSAGE_TEST_TAG),
                )
            } else {
                genres.forEach { genre ->
                    GenreRow(
                        genre = genre,
                        modifier = Modifier.testTag(StatisticsTestTags.genreRow(genre.name)),
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreRow(
    genre: GenreSlice,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = genre.caption,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.xxxSmall))

        StatisticBar(
            fraction = genre.fraction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun GenreSectionPreview() {
    GenreSection(
        genres = persistentListOf(
            GenreSlice(name = "Drama", showCount = 24, caption = "24 shows", fraction = 1f),
            GenreSlice(name = "Comedy", showCount = 11, caption = "11 shows", fraction = 0.45f),
            GenreSlice(name = "Crime", showCount = 9, caption = "9 shows", fraction = 0.37f),
            GenreSlice(name = "Science Fiction", showCount = 7, caption = "7 shows", fraction = 0.29f),
            GenreSlice(name = "Thriller", showCount = 5, caption = "5 shows", fraction = 0.2f),
            GenreSlice(name = "Action", showCount = 4, caption = "4 shows", fraction = 0.16f),
            GenreSlice(name = "Other", showCount = 12, caption = "12 shows", fraction = 0.5f),
        ),
        title = "Genres you watch",
        emptyMessage = "",
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun GenreSectionEmptyPreview() {
    GenreSection(
        genres = persistentListOf(),
        title = "Genres you watch",
        emptyMessage = "We do not know the genres of the shows you have watched yet.",
    )
}
