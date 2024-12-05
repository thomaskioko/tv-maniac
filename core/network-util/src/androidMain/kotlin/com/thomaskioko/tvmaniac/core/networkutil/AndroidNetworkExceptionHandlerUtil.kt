package com.thomaskioko.tvmaniac.core.networkutil

import com.thomaskioko.tvmaniac.core.base.model.Configs
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.serialization.JsonConvertException
import java.net.UnknownHostException
import kotlinx.serialization.SerializationException
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AndroidNetworkExceptionHandlerUtil(
  private val configs: Configs,
) : NetworkExceptionHandler {
  val errorMessage = "Something went wrong"

  override fun resolveError(throwable: Throwable) =
    when (throwable) {
      is UnknownHostException -> "No Internet Connection!"
      is ClientRequestException -> {
        if (configs.isDebug) {
          "Status Code ${throwable.response.status.value}: Missing Api Key"
        } else {
          errorMessage
        }
      }
      is JsonConvertException,
      is SerializationException,
      is NoTransformationFoundException -> if (configs.isDebug) throwable.message else errorMessage
      else -> errorMessage
    }
}
