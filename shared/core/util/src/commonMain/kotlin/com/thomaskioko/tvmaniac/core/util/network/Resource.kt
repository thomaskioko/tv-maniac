package com.thomaskioko.tvmaniac.core.util.network

/**
 * A generic class that describes data with a status
 */
/**
 * A generic class that holds a value with after a request is done.
 * @param <T>
</T> */
sealed class Resource<T>(
    val data: T? = null,
    val errorMessage: String = ""
) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(errorMessage: String, data: T? = null) : Resource<T>(data, errorMessage)
}