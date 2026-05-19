package com.thomaskioko.tvmaniac.featureflags.presenter

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class FeatureFlagsState(
    val items: ImmutableList<FeatureFlagItem> = persistentListOf(),
    val searchQuery: String = "",
    val sort: FeatureFlagSortDescriptor = FeatureFlagSortDescriptor.Date,
    val ascending: Boolean = false,
    val groupByType: Boolean = false,
    val title: String = "",
    val searchHint: String = "",
    val resetAllTitle: String = "",
    val resetAllSubtitle: String = "",
    val forceRefreshTitle: String = "",
    val forceRefreshSubtitle: String = "",
    val emptyResults: String = "",
    val resetButtonLabel: String = "",
    val moreActionsLabel: String = "",
    val groupByTypeLabel: String = "",
    val noGroupingLabel: String = "",
    val sortAscendingLabel: String = "",
    val sortDescendingLabel: String = "",
) {
    public companion object {
        public val DEFAULT_STATE: FeatureFlagsState = FeatureFlagsState()
    }
}

public data class FeatureFlagItem(
    val flag: FeatureFlag,
    val title: String,
    val description: String,
    val source: String,
    val value: Boolean,
    val isLocal: Boolean,
)
