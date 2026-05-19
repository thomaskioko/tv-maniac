package com.thomaskioko.tvmaniac.featureflags.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.presenter.FeatureFlagItem
import com.thomaskioko.tvmaniac.featureflags.presenter.FeatureFlagsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal class FeatureFlagsPreviewParameterProvider : PreviewParameterProvider<FeatureFlagsState> {
    override val values: Sequence<FeatureFlagsState>
        get() = sequenceOf(
            defaultFeatureFlagsState,
            localSourceFeatureFlagsState,
            emptyFeatureFlagsState,
        )
}

internal val defaultFeatureFlagsState: FeatureFlagsState = baseState(
    items = previewItems(sourceLabel = "Firebase", isLocal = false),
)

internal val localSourceFeatureFlagsState: FeatureFlagsState = baseState(
    items = previewItems(sourceLabel = "Local", isLocal = true, value = true),
    forceRefreshSubtitle = "Last fetched at 2026-05-18 09:42",
)

internal val emptyFeatureFlagsState: FeatureFlagsState = baseState(
    searchQuery = "missing_flag",
    items = persistentListOf(),
)

private fun baseState(
    items: ImmutableList<FeatureFlagItem> = persistentListOf(),
    searchQuery: String = "",
    forceRefreshSubtitle: String = "Fetch latest values from Remote Config",
) = FeatureFlagsState(
    items = items,
    searchQuery = searchQuery,
    title = "Feature Flags",
    searchHint = "Search flags",
    resetAllTitle = "Reset all",
    resetAllSubtitle = "Clear feature flags or reset to default",
    forceRefreshTitle = "Force refresh",
    forceRefreshSubtitle = forceRefreshSubtitle,
    emptyResults = "No flags match this query",
    resetButtonLabel = "Reset",
    moreActionsLabel = "More actions",
    groupByTypeLabel = "Group by type",
    noGroupingLabel = "No grouping",
    sortAscendingLabel = "Sort ascending",
    sortDescendingLabel = "Sort descending",
)

private fun previewItems(
    sourceLabel: String,
    isLocal: Boolean,
    value: Boolean = false,
) = FeatureFlag.entries
    .map { flag ->
        FeatureFlagItem(
            flag = flag,
            title = flag.title,
            description = flag.description,
            source = sourceLabel,
            value = if (isLocal) value else flag.defaultValue,
            isLocal = isLocal,
        )
    }
    .toImmutableList()
