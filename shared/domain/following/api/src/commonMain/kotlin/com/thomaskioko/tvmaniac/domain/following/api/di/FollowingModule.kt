package com.thomaskioko.tvmaniac.domain.following.api.di

import com.thomaskioko.tvmaniac.domain.following.api.FollowingStateMachine
import org.koin.core.module.Module
import org.koin.dsl.module

val followingModule : Module = module {
    single { FollowingStateMachine(get()) }
}