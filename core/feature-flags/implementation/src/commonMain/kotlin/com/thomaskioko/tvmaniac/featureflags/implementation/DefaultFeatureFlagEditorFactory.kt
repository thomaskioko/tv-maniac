package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagEditor
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagEditorFactory
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFeatureFlagEditorFactory : FeatureFlagEditorFactory {

    override fun <Value : Any> create(
        flag: FeatureFlag,
        onSet: suspend (FeatureFlag, Value) -> Unit,
        onClear: suspend (FeatureFlag) -> Unit,
    ): FeatureFlagEditor<Value> = DefaultFeatureFlagEditor(
        flag = flag,
        onSet = onSet,
        onClear = onClear,
    )
}
