package com.thomaskioko.tvmaniac.settings

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun settingsDomainModule(): Module = module {
    factory { SettingsStateMachine(get()) }
}