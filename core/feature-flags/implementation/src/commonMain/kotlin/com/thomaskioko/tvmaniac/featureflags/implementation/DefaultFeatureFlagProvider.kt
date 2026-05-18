package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagProvider
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFeatureFlagProvider(
    private val localStore: FeatureFlagLocalStore,
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
