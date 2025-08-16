package com.thomaskioko.tvmaniac.testing.di

import kotlin.reflect.KClass

expect inline fun <reified T : Any> KClass<T>.create(): T
