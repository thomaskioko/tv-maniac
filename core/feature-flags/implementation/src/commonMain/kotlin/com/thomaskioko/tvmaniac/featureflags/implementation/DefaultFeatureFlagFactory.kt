package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagFactory
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.RemoteFlag
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.datetime.LocalDate

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, binding = binding<FeatureFlagFactory>())
public class DefaultFeatureFlagFactory(
    private val remote: FeatureFlagsRemoteConfig,
) : FeatureFlagFactory {

    override fun boolean(
        key: String,
        title: String,
        description: String,
        defaultValue: Boolean,
        dateAdded: LocalDate,
    ): FeatureFlag<Boolean> = object : RemoteFlag(
        key = key,
        title = title,
        description = description,
        dateAdded = dateAdded,
        defaultValue = defaultValue,
        remote = remote,
    ) {}
}
