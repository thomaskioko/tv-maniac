package com.thomaskioko.tvmaniac.core.base.coroutines

internal data class LoggedError(
    val tag: String?,
    val message: String,
    val throwable: Throwable?,
)
