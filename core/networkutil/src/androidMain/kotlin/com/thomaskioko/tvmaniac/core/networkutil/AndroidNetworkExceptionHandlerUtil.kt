package com.thomaskioko.tvmaniac.core.networkutil

import com.thomaskioko.tvmaniac.util.model.Configs
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.serialization.JsonConvertException
import me.tatarka.inject.annotations.Inject
import java.net.UnknownHostException

@Inject
class AndroidNetworkExceptionHandlerUtil(
    private val configs: Configs,
) : NetworkExceptionHandler {
    val errorMessage = "Something went wrong"

    override fun resolveError(throwable: Throwable) =
        when (throwable) {
            is HttpExceptions -> if (configs.isDebug) throwable.message else errorMessage
            is ClientRequestException -> {
                if (configs.isDebug) {
                    "${throwable.response.status.value} Missing Api Key"
                } else {
                    errorMessage
                }
            }

            is UnknownHostException -> "No Internet Connection!"
            is JsonConvertException -> "Error Parsing Response"
            is NoTransformationFoundException -> if (configs.isDebug) throwable.message else errorMessage
            else -> errorMessage
        }
}
