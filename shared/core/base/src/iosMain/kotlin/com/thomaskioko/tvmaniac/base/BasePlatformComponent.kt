package com.thomaskioko.tvmaniac.base

import com.thomaskioko.tvmaniac.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.base.scope.NsQueueCoroutineScope
import com.thomaskioko.tvmaniac.base.util.AppUtils
import com.thomaskioko.tvmaniac.base.util.DateFormatter
import com.thomaskioko.tvmaniac.base.util.ExceptionHandler
import com.thomaskioko.tvmaniac.base.util.FormatterUtil
import com.thomaskioko.tvmaniac.base.util.IosAppUtils
import com.thomaskioko.tvmaniac.base.util.IosDateFormatter
import com.thomaskioko.tvmaniac.base.util.IosExceptionHandler
import com.thomaskioko.tvmaniac.base.util.IosFormatterUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.tatarka.inject.annotations.Provides

actual interface BasePlatformComponent {

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
        io = NsQueueCoroutineScope(),
        main = NsQueueCoroutineScope(),
    )
}
