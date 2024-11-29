package com.thomaskioko.tvmaniac.util.inject

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
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
actual interface UtilPlatformComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideIosFormatterUtil(bind: IosFormatterUtil): FormatterUtil = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideAppUtils(bind: IosAppUtils): AppUtils = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideBundleProvider(): BundleProvider = BundleProvider(NSBundle.mainBundle)

  @SingleIn(AppScope::class)
  @Provides
  fun provideConfigs(resourceReader: YamlResourceReader): Configs =
    resourceReader.readAndDecodeResource("config.yaml", Configs.serializer())

  @SingleIn(AppScope::class)
  @Provides
  fun provideResourceReader(bind: BundleResourceReader): ResourceReader = bind
}
