package com.thomaskioko.tvmaniac.util.inject

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.util.AndroidAppUtils
import com.thomaskioko.tvmaniac.util.AndroidFormatterUtil
import com.thomaskioko.tvmaniac.util.AndroidNetworkExceptionHandlerUtil
import com.thomaskioko.tvmaniac.util.AppUtils
import com.thomaskioko.tvmaniac.util.ClasspathResourceReader
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.util.ResourceReader
import com.thomaskioko.tvmaniac.util.YamlResourceReader
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.util.model.Configs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.tatarka.inject.annotations.Provides

actual interface UtilPlatformComponent {

  @ApplicationScope
  @Provides
  fun provideCoroutineDispatchers(): AppCoroutineDispatchers =
    AppCoroutineDispatchers(
      io = Dispatchers.IO,
      computation = Dispatchers.Default,
      main = Dispatchers.Main,
    )

  @ApplicationScope
  @Provides
  fun provideCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
    AppCoroutineScope(
      default = CoroutineScope(Job() + dispatchers.computation),
      io = CoroutineScope(Job() + dispatchers.io),
      main = CoroutineScope(Job() + dispatchers.main),
    )

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

  @ApplicationScope
  @Provides
  fun provideNetworkExceptionHandler(
    bind: AndroidNetworkExceptionHandlerUtil,
  ): NetworkExceptionHandler = bind
}
