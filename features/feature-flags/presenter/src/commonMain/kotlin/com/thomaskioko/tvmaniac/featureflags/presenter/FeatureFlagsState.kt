package com.thomaskioko.tvmaniac.featureflags.presenter

import com.thomaskioko.tvmaniac.domain.featureflags.FeatureFlagRow
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Instant

public data class FeatureFlagsState(
    val rows: ImmutableList<FeatureFlagRow> = persistentListOf(),
    val searchQuery: String = "",
    val sort: FeatureFlagSortDescriptor = FeatureFlagSortDescriptor.Date,
    val ascending: Boolean = false,
    val groupByType: Boolean = false,
    val lastFetchedAt: Instant? = null,
) {
    public companion object {
        public val DEFAULT_STATE: FeatureFlagsState = FeatureFlagsState()
    }
}
