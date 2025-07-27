package com.thomaskioko.tvmaniac.util.di

import android.app.Application
import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.util.YamlResourceReader
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface UtilPlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideConfigs(resourceReader: YamlResourceReader, application: Application): Configs {
        val configFileName = if (application.packageName.endsWith(".debug")) {
            "dev.yaml"
        } else {
            "production.yaml"
        }
        return resourceReader.readAndDecodeResource(configFileName, Configs.serializer())
    }
}
