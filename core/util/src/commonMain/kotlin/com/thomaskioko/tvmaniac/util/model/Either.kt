package com.thomaskioko.tvmaniac.util.model

sealed class Either<out L, out R> {

    data class Left<out L>(val error: L) : Either<L, Nothing>()

    data class Right<out R>(val data: R) : Either<Nothing, R>()

    fun <T> fold(lfn: (L) -> T, rfn: (R?) -> T): T = when (this) {
        is Left -> lfn(error)
        is Right -> rfn(data)
    }

    fun getOrNull(): R? = (this as? Right)?.data
    fun getErrorOrNull(): L? = (this as? Left)?.error
}
