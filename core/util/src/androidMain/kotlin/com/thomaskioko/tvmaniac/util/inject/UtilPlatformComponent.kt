package com.thomaskioko.tvmaniac.util.inject

import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.util.AndroidAppUtils
import com.thomaskioko.tvmaniac.util.AndroidFormatterUtil
import com.thomaskioko.tvmaniac.util.AppUtils
import com.thomaskioko.tvmaniac.util.ClasspathResourceReader
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.ResourceReader
import com.thomaskioko.tvmaniac.util.YamlResourceReader
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface UtilPlatformComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideConfigs(resourceReader: YamlResourceReader): Configs =
    resourceReader.readAndDecodeResource("config.yaml", Configs.serializer())
}
