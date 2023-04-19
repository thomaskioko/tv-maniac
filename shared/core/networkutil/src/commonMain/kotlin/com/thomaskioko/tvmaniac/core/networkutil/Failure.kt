package com.thomaskioko.tvmaniac.core.networkutil

sealed class Failure(
    val throwable: Throwable,
    val errorMessage: String = "",
)

class DefaultError(val message: String) : Failure(
    throwable = Throwable(message),
    errorMessage = message,
)
