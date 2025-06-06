package com.thomaskioko.tvmaniac.core.networkutil.model

/**
 * Represents a value of one of two possible types (a disjoint union). Instances of [Either] are
 * either an instance of [Left] or [Right]. FP Convention dictates that: [Left] is used for
 * "failure". [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */
sealed class Either<out L, out R> {

    /** Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val left: L) : Either<L, Nothing>()

    /** Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val right: R) : Either<Nothing, R>()

    fun <T> fold(onFailure: (L) -> T, onSuccess: (R) -> T): T =
        when (this) {
            is Left -> onFailure(left)
            is Right -> onSuccess(right)
        }

    fun getOrNull(): R? = (this as? Right)?.right

    fun getErrorOrNull(): L? = (this as? Left)?.left
}
