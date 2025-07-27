package com.thomaskioko.tvmaniac.util.di

import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.util.YamlResourceReader
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSBundle

@ContributesTo(AppScope::class)
interface UtilPlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideConfigs(resourceReader: YamlResourceReader): Configs {
        val bundleIdentifier = NSBundle.mainBundle.bundleIdentifier ?: ""
        val configFileName = if (bundleIdentifier.endsWith(".dev")) {
            "dev.yaml"
        } else {
            "production.yaml"
        }

        return resourceReader.readAndDecodeResource(configFileName, Configs.serializer())
    }
}
