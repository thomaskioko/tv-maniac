package com.thomaskioko.tvmaniac.appconfig

import com.thomaskioko.tvmaniac.core.base.IsDebugBuild
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultDebugConfig(
    @IsDebugBuild override val isDebug: Boolean,
) : DebugConfig
