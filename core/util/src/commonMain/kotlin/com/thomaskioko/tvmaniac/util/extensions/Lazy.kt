package com.thomaskioko.tvmaniac.util.extensions

@Suppress("NOTHING_TO_INLINE")
inline fun <T> unsafeLazy(noinline initializer: () -> T): Lazy<T> =
  lazy(LazyThreadSafetyMode.NONE, initializer)
