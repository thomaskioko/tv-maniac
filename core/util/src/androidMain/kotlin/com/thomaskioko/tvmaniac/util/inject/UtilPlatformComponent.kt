package com.thomaskioko.tvmaniac.util.inject

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.util.AndroidAppUtils
import com.thomaskioko.tvmaniac.util.AndroidFormatterUtil
import com.thomaskioko.tvmaniac.util.AppUtils
import com.thomaskioko.tvmaniac.util.ClasspathResourceReader
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.ResourceReader
import com.thomaskioko.tvmaniac.util.YamlResourceReader
import me.tatarka.inject.annotations.Provides

actual interface UtilPlatformComponent {

  @ApplicationScope
  @Provides
  fun provideAndroidFormatterUtil(bind: AndroidFormatterUtil): FormatterUtil = bind

  @ApplicationScope @Provides fun provideAppUtils(bind: AndroidAppUtils): AppUtils = bind

  @ApplicationScope
  @Provides
  fun provideConfigs(resourceReader: YamlResourceReader): Configs =
    resourceReader.readAndDecodeResource("config.yaml", Configs.serializer())

  @ApplicationScope
  @Provides
  fun provideResourceReader(bind: ClasspathResourceReader): ResourceReader = bind
}
