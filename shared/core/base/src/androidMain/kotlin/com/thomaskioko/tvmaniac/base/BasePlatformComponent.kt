package com.thomaskioko.tvmaniac.base

import com.thomaskioko.tvmaniac.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.base.util.AndroidAppUtils
import com.thomaskioko.tvmaniac.base.util.AndroidDateFormatter
import com.thomaskioko.tvmaniac.base.util.AndroidExceptionHandler
import com.thomaskioko.tvmaniac.base.util.AndroidFormatterUtil
import com.thomaskioko.tvmaniac.base.util.AppUtils
import com.thomaskioko.tvmaniac.base.util.DateFormatter
import com.thomaskioko.tvmaniac.base.util.ExceptionHandler
import com.thomaskioko.tvmaniac.base.util.FormatterUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.tatarka.inject.annotations.Provides

actual interface BasePlatformComponent {

    @ApplicationScope
    @Provides
    fun provideCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main,
    )

    @ApplicationScope
    @Provides
    fun provideCoroutineScope(
        dispatchers: AppCoroutineDispatchers
    ): AppCoroutineScope = AppCoroutineScope(
        default = CoroutineScope(Job() + dispatchers.computation),
        io = CoroutineScope(Job() + dispatchers.io),
        main = CoroutineScope(Job() + dispatchers.main),
    )

    @ApplicationScope
    @Provides
    fun provideDateFormatter(bind: AndroidDateFormatter): DateFormatter = bind

    @ApplicationScope
    @Provides
    fun provideAndroidFormatterUtil(bind: AndroidFormatterUtil): FormatterUtil = bind

    @ApplicationScope
    @Provides
    fun provideAndroidExceptionHandler(bind: AndroidExceptionHandler): ExceptionHandler = bind

    @ApplicationScope
    @Provides
    fun provideAppUtils(bind: AndroidAppUtils): AppUtils = bind
}
