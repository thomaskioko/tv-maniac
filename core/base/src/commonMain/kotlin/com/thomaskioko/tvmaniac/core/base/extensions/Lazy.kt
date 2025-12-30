package com.thomaskioko.tvmaniac.core.base.extensions

@Suppress("NOTHING_TO_INLINE")
public inline fun <T> unsafeLazy(noinline initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, initializer)
