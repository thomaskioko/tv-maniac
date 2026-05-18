package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagEditor
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag

public class DefaultFeatureFlagEditor<Value : Any>(
    override val flag: FeatureFlag,
    private val onSet: suspend (FeatureFlag, Value) -> Unit,
    private val onClear: suspend (FeatureFlag) -> Unit,
) : FeatureFlagEditor<Value> {

    override suspend fun set(value: Value?) {
        if (value == null) onClear(flag) else onSet(flag, value)
    }
}
