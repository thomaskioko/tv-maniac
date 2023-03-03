package com.thomaskioko.tvmaniac.core.util.di

import com.thomaskioko.tvmaniac.core.util.AppContext
import com.thomaskioko.tvmaniac.core.util.AppUtils
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelperImpl
import com.thomaskioko.tvmaniac.core.util.scope.CoroutineScopeProvider
import com.thomaskioko.tvmaniac.core.util.scope.DispatcherMain
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun coreUtilModule(): Module = module {

    single<DateUtilHelper> { DateUtilHelperImpl() }
    single{ AppUtils(context = AppContext()) }
    single { DispatcherMain() }
    single { CoroutineScopeProvider() }
}
