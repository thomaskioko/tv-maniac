package com.thomaskioko.tvmaniac.core.networkutil.model

sealed class Failure(
    val throwable: Throwable,
    val errorMessage: String? = "",
)

class DefaultError(val message: String?) :
    Failure(
        throwable = Throwable(message),
        errorMessage = message,
    )

data class ServerError(val message: String?) :
    Failure(
        throwable = Throwable(message),
        errorMessage = message,
    )
