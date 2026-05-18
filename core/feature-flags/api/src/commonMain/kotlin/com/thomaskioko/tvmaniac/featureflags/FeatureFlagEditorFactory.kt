package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag

public interface FeatureFlagEditorFactory {
    public fun <Value : Any> create(
        flag: FeatureFlag,
        onSet: suspend (FeatureFlag, Value) -> Unit,
        onClear: suspend (FeatureFlag) -> Unit,
    ): FeatureFlagEditor<Value>
}
