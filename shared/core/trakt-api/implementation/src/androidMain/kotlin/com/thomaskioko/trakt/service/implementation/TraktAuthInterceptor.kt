package com.thomaskioko.trakt.service.implementation

import android.content.SharedPreferences
import com.thomaskioko.tvmaniac.util.model.Configs
import io.ktor.http.HttpHeaders
import me.tatarka.inject.annotations.Inject
import net.openid.appauth.AuthState
import okhttp3.Interceptor
import okhttp3.Response

@Inject
class TraktAuthInterceptor(
    private val configs: Configs,
    private val authPrefs: SharedPreferences,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val stateJson = authPrefs.getString("stateJson", null)
        val authState = stateJson?.let { AuthState.jsonDeserialize(it) }

        request = request.newBuilder()
            .addHeader(HttpHeaders.ContentType, "application/json")
            .addHeader(HttpHeaders.Authorization, "Bearer ${authState?.accessToken}")
            .addHeader("trakt-api-version", "2")
            .addHeader("trakt-api-key", configs.traktClientId)
            .build()
        return chain.proceed(request)
    }
}
