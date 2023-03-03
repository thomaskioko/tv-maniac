package com.thomaskioko.tvmaniac.core.util.network

import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError

sealed class Failure(
    val throwable: Throwable,
    val errorMessage: String = ""
)

class DefaultError(val exception: Throwable) : Failure(
    throwable = exception,
    errorMessage = exception.resolveError()
)