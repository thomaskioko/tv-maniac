package com.thomaskioko.tvmaniac.showdetails.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacChip
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.presenter.showdetails.model.SeasonModel
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

@Composable
internal fun SeasonChipItem(
    season: SeasonModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TvManiacChip(
        modifier = modifier.testTag(ShowDetailsTestTags.seasonChip(season.seasonNumber)),
        text = season.name,
        selected = isSelected,
        onClick = onClick,
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SeasonChipItemPreview() {
    SeasonChipItem(
        season = SeasonModel(
            seasonId = 1L,
            tvShowId = 1L,
            name = "Season 1",
            seasonNumber = 1L,
            watchedCount = 4,
            totalCount = 6,
        ),
        isSelected = false,
        onClick = {},
        modifier = Modifier.padding(4.dp),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SeasonChipItemSelectedPreview() {
    SeasonChipItem(
        season = SeasonModel(
            seasonId = 1L,
            tvShowId = 1L,
            name = "Season 1",
            seasonNumber = 1L,
            watchedCount = 6,
            totalCount = 6,
        ),
        isSelected = true,
        onClick = {},
        modifier = Modifier.padding(4.dp),
    )
}
