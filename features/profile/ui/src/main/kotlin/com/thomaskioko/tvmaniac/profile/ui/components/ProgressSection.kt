package com.thomaskioko.tvmaniac.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.InlineSectionError
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageDimens
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileShowItem
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.testtags.component.CollapsibleSectionTestTags
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal enum class ProgressFilter { COMPLETED, IN_PROGRESS }

@Composable
internal fun ProgressSection(
    inProgress: SectionState<ProfileShowItem>,
    completed: SectionState<ProfileShowItem>,
    title: String,
    inProgressLabel: String,
    completedLabel: String,
    emptyLabel: String,
    retryLabel: String,
    onShowClick: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (inProgress is SectionState.Empty && completed is SectionState.Empty) return

    var selectedFilter by rememberSaveable { mutableStateOf(ProgressFilter.COMPLETED) }

    CollapsibleSection(
        title = title,
        modifier = modifier,
        toggleTestTag = CollapsibleSectionTestTags.toggle(ProfileTestTags.PROGRESS_SECTION_KEY),
        contentSpacing = 0.dp,
    ) {
        val posterWidth = Layout.posterWidth

        Column {
            FilterRow(
                selected = selectedFilter,
                inProgressLabel = inProgressLabel,
                completedLabel = completedLabel,
                onSelected = { selectedFilter = it },
            )

            Spacer(modifier = Modifier.height(2.dp))

            val sectionState = when (selectedFilter) {
                ProgressFilter.IN_PROGRESS -> inProgress
                ProgressFilter.COMPLETED -> completed
            }

            when (sectionState) {
                SectionState.Loading -> PosterSkeletonRow(posterWidth = posterWidth)
                is SectionState.Error -> InlineSectionError(
                    message = sectionState.message.message,
                    retryLabel = retryLabel,
                    onRetry = onRetry,
                    retryModifier = Modifier.testTag(ProfileTestTags.PROGRESS_RETRY_TEST_TAG),
                )
                is SectionState.Content -> PosterRow(
                    shows = sectionState.items,
                    posterWidth = posterWidth,
                    onShowClick = onShowClick,
                )
                SectionState.Empty -> EmptyLabel(label = emptyLabel)
            }
        }
    }
}

@Composable
private fun FilterRow(
    selected: ProgressFilter,
    inProgressLabel: String,
    completedLabel: String,
    onSelected: (ProgressFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ProgressChip(
            label = completedLabel,
            icon = Icons.Filled.DoneAll,
            selected = selected == ProgressFilter.COMPLETED,
            onClick = { onSelected(ProgressFilter.COMPLETED) },
            modifier = Modifier.testTag(ProfileTestTags.PROGRESS_COMPLETED_CHIP_TEST_TAG),
        )
        ProgressChip(
            label = inProgressLabel,
            icon = Icons.Filled.HourglassEmpty,
            selected = selected == ProgressFilter.IN_PROGRESS,
            onClick = { onSelected(ProgressFilter.IN_PROGRESS) },
            modifier = Modifier.testTag(ProfileTestTags.PROGRESS_IN_PROGRESS_CHIP_TEST_TAG),
        )
    }
}

@Composable
private fun ProgressChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, style = MaterialTheme.typography.labelLarge) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize),
            )
        },
        shape = MaterialTheme.shapes.small,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
            labelColor = MaterialTheme.colorScheme.secondary,
            iconColor = MaterialTheme.colorScheme.secondary,
            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f),
            selectedLabelColor = MaterialTheme.colorScheme.secondary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.secondary,
        ),
    )
}

@Composable
private fun PosterRow(
    shows: ImmutableList<ProfileShowItem>,
    posterWidth: Dp,
    onShowClick: (Long) -> Unit,
) {
    LazyRow(
        modifier = Modifier.testTag(ProfileTestTags.PROGRESS_ROW_TEST_TAG),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = shows,
            key = { it.traktId },
        ) { show ->
            PosterCard(
                imageUrl = show.posterUrl,
                title = show.title,
                imageWidth = posterWidth,
                shape = MaterialTheme.shapes.medium,
                onClick = { onShowClick(show.traktId) },
                modifier = Modifier.testTag(ProfileTestTags.showCard(show.traktId)),
            )
        }
    }
}

@Composable
private fun PosterSkeletonRow(posterWidth: Dp) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(3) {
            ShimmerBox(
                modifier = Modifier
                    .width(posterWidth)
                    .height(posterWidth / ImageDimens.PosterAspect),
                shape = MaterialTheme.shapes.medium,
            )
        }
    }
}

@Composable
private fun EmptyLabel(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ProgressSectionPreview() {
    ProgressSection(
        inProgress = SectionState.Content(
            persistentListOf(
                ProfileShowItem(traktId = 1, tmdbId = 1396, title = "Breaking Bad", posterUrl = null),
                ProfileShowItem(traktId = 2, tmdbId = 1399, title = "Game of Thrones", posterUrl = null),
            ),
        ),
        completed = SectionState.Content(
            persistentListOf(
                ProfileShowItem(traktId = 3, tmdbId = 66732, title = "Stranger Things", posterUrl = null),
            ),
        ),
        title = "Progress",
        inProgressLabel = "In Progress",
        completedLabel = "Completed",
        emptyLabel = "Nothing here yet",
        retryLabel = "Retry",
        onShowClick = {},
        onRetry = {},
    )
}
