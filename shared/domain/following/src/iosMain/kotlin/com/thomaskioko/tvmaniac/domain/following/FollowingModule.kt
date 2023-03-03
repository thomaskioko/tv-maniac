package com.thomaskioko.tvmaniac.domain.following

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun followingDomainModule(): Module = module {
    factory { FollowingStateMachine(get()) }
}
