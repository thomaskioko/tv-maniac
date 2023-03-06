package com.thomaskioko.tvmaniac.core.util.di

import com.thomaskioko.tvmaniac.core.util.AppContext
import com.thomaskioko.tvmaniac.core.util.AppUtils
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelperImpl
import com.thomaskioko.tvmaniac.core.util.scope.CoroutineScopeProvider
import com.thomaskioko.tvmaniac.core.util.scope.DispatcherProvider
import com.thomaskioko.tvmaniac.core.util.scope.NsQueueCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun coreUtilModule(): Module = module {

    single<DateUtilHelper> { DateUtilHelperImpl() }
    single { AppUtils(context = AppContext()) }
    single { Dispatchers.Default }
    single { CoroutineScopeProvider() }
    single(named("nsQueueCoroutineScope"))  { NsQueueCoroutineScope() }
    single(named("mainScope")) { MainScope() }
    single(named("defaultProvider")) { DispatcherProvider().default }
}
