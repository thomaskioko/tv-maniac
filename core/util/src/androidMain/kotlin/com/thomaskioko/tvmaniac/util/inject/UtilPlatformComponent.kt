package com.thomaskioko.tvmaniac.util.inject

import android.app.Application
import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.util.YamlResourceReader
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface UtilPlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideConfigs(resourceReader: YamlResourceReader, application: Application): Configs {
        val configFileName = if (application.packageName.endsWith(".debug")) {
            "dev.yaml"
        } else {
            "prod.yaml"
        }
        return resourceReader.readAndDecodeResource(configFileName, Configs.serializer())
    }
}
