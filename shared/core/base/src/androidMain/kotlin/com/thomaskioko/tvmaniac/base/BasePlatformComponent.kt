package com.thomaskioko.tvmaniac.base

import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.base.util.AndroidDateFormatter
import com.thomaskioko.tvmaniac.base.util.AndroidExceptionHandler
import com.thomaskioko.tvmaniac.base.util.AndroidFormatterUtil
import com.thomaskioko.tvmaniac.base.util.DateFormatter
import com.thomaskioko.tvmaniac.base.util.ExceptionHandler
import com.thomaskioko.tvmaniac.base.util.FormatterUtil
import me.tatarka.inject.annotations.Provides

interface BasePlatformComponent {

    @ApplicationScope
    @Provides
    fun provideDateFormatter(bind: AndroidDateFormatter): DateFormatter = bind

    @ApplicationScope
    @Provides
    fun provideAndroidFormatterUtil(bind: AndroidFormatterUtil): FormatterUtil = bind

    @ApplicationScope
    @Provides
    fun provideAndroidExceptionHandler(bind: AndroidExceptionHandler): ExceptionHandler = bind
}
