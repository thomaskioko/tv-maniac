package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagProvider
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor

public class FakeFeatureFlagProvider(
    private val localStore: FeatureFlagLocalStore = FakeFeatureFlagLocalStore(),
) : FeatureFlagProvider {

    override fun flags(sort: FeatureFlagSortDescriptor, ascending: Boolean): List<FeatureFlag> {
        val comparator: Comparator<FeatureFlag> = when (sort) {
            FeatureFlagSortDescriptor.Title -> compareBy { it.title }
            FeatureFlagSortDescriptor.Key -> compareBy { it.key }
            FeatureFlagSortDescriptor.Date -> compareBy { it.dateAdded }
        }
        return FeatureFlag.entries.sortedWith(if (ascending) comparator else comparator.reversed())
    }

    override fun findFeatureFlag(key: String): FeatureFlag? =
        FeatureFlag.entries.firstOrNull { it.key == key }

    override suspend fun resetAllLocals() {
        localStore.clearAll()
    }
}
