package com.thomaskioko.tvmaniac.featureflags.presenter

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor

public sealed interface FeatureFlagsActions

public data object BackClicked : FeatureFlagsActions

public data class ToggleFlag(val key: String, val value: Boolean) : FeatureFlagsActions

public data class ClearLocal(val key: String) : FeatureFlagsActions

public data object ClearAllLocals : FeatureFlagsActions

public data object ForceRefresh : FeatureFlagsActions

public data class SearchQueryChanged(val query: String) : FeatureFlagsActions

public data class SortChanged(val sort: FeatureFlagSortDescriptor) : FeatureFlagsActions

public data object DirectionToggled : FeatureFlagsActions

public data object GroupByTypeToggled : FeatureFlagsActions
