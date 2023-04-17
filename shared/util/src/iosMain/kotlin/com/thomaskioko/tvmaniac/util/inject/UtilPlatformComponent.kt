package com.thomaskioko.tvmaniac.util.inject

import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.util.model.Configs
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import com.thomaskioko.tvmaniac.util.scope.NsQueueCoroutineScope
import com.thomaskioko.tvmaniac.util.AppUtils
import com.thomaskioko.tvmaniac.util.BundleProvider
import com.thomaskioko.tvmaniac.util.BundleResourceReader
import com.thomaskioko.tvmaniac.util.DateFormatter
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.IosAppUtils
import com.thomaskioko.tvmaniac.util.IosDateFormatter
import com.thomaskioko.tvmaniac.util.IosExceptionHandler
import com.thomaskioko.tvmaniac.util.IosFormatterUtil
import com.thomaskioko.tvmaniac.util.ResourceReader
import com.thomaskioko.tvmaniac.util.YamlResourceReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSBundle

actual interface UtilPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideDateFormatter(bind: IosDateFormatter): DateFormatter = bind

    @ApplicationScope
    @Provides
    fun provideIosFormatterUtil(bind: IosFormatterUtil): FormatterUtil = bind

    @ApplicationScope
    @Provides
    fun provideExceptionHandler(bind: IosExceptionHandler): ExceptionHandler = bind

    @ApplicationScope
    @Provides
    fun provideAppUtils(bind: IosAppUtils): AppUtils = bind

    @ApplicationScope
    @Provides
    fun provideCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.Default,
        computation = Dispatchers.Default,
        main = Dispatchers.Main,
    )

    @ApplicationScope
    @Provides
    fun provideAppCoroutineScope(
        dispatchers: AppCoroutineDispatchers
    ): AppCoroutineScope = AppCoroutineScope(
        default = CoroutineScope(Job() + dispatchers.computation),
        io = CoroutineScope(Job() + dispatchers.io),
        main = NsQueueCoroutineScope(),
    )

    @ApplicationScope
    @Provides
    fun provideBundleProvider(): BundleProvider = BundleProvider(NSBundle.mainBundle)

    @ApplicationScope
    @Provides
    fun provideConfigs(
        resourceReader: YamlResourceReader
    ): Configs = resourceReader.readAndDecodeResource("config.yaml", Configs.serializer())


    @ApplicationScope
    @Provides
    fun provideResourceReader(bind: BundleResourceReader): ResourceReader = bind
}