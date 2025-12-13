package com.thomaskioko.tvmaniac.showdetails.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacChip
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presenter.showdetails.model.SeasonModel

@Composable
internal fun SeasonProgressCard(
    season: SeasonModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TvManiacChip(
        modifier = modifier,
        text = season.name,
        selected = isSelected,
        onClick = onClick,
    )
}

@ThemePreviews
@Composable
private fun SeasonProgressCardPreview() {
    TvManiacTheme {
        Surface {
            SeasonProgressCard(
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
    }
}

@ThemePreviews
@Composable
private fun SeasonProgressCardSelectedPreview() {
    TvManiacTheme {
        Surface {
            SeasonProgressCard(
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
    }
}
