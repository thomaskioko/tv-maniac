package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchTime
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags

@Composable
internal fun WatchTimeSection(
    watchTime: WatchTime,
    title: String,
    daysLabel: String,
    hoursLabel: String,
    minutesLabel: String,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = title,
        modifier = modifier.testTag(StatisticsTestTags.WATCH_TIME_HERO_TEST_TAG),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = TvManiacElevation.small),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(TvManiacSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.large),
            ) {
                WatchTimeSegment(
                    value = watchTime.days,
                    label = daysLabel,
                    modifier = Modifier.weight(1f),
                )
                WatchTimeSegment(
                    value = watchTime.hours,
                    label = hoursLabel,
                    modifier = Modifier.weight(1f),
                )
                WatchTimeSegment(
                    value = watchTime.minutes,
                    label = minutesLabel,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WatchTimeSegment(
    value: Long,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = TvManiacTheme.colorScheme.accent,
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.xxxSmall))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = SectionLabelLetterSpacing,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchTimeSectionPreview() {
    WatchTimeSection(
        watchTime = WatchTime(days = 12, hours = 4, minutes = 30),
        title = "Total time watched",
        daysLabel = "days",
        hoursLabel = "hours",
        minutesLabel = "minutes",
    )
}
