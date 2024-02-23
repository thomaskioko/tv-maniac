package com.thomaskioko.tvmaniac.util.inject

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.util.AppUtils
import com.thomaskioko.tvmaniac.util.BundleProvider
import com.thomaskioko.tvmaniac.util.BundleResourceReader
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.IosAppUtils
import com.thomaskioko.tvmaniac.util.IosFormatterUtil
import com.thomaskioko.tvmaniac.util.ResourceReader
import com.thomaskioko.tvmaniac.util.YamlResourceReader
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSBundle

actual interface UtilPlatformComponent {

  @ApplicationScope
  @Provides
  fun provideIosFormatterUtil(bind: IosFormatterUtil): FormatterUtil = bind

  @ApplicationScope @Provides fun provideAppUtils(bind: IosAppUtils): AppUtils = bind

  @ApplicationScope
  @Provides
  fun provideBundleProvider(): BundleProvider = BundleProvider(NSBundle.mainBundle)

  @ApplicationScope
  @Provides
  fun provideConfigs(resourceReader: YamlResourceReader): Configs =
    resourceReader.readAndDecodeResource("config.yaml", Configs.serializer())

  @ApplicationScope
  @Provides
  fun provideResourceReader(bind: BundleResourceReader): ResourceReader = bind
}
