package com.thomaskioko.tvmaniac.trakt.implementation

import android.content.SharedPreferences
import io.ktor.http.HttpHeaders
import net.openid.appauth.AuthState
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class TraktAuthInterceptor @Inject constructor(
    @Named("trakt-client-id") private val clientId: String,
    @Named("auth") private val authPrefs: SharedPreferences,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val stateJson = authPrefs.getString("stateJson", null)
        val authState = stateJson?.let { AuthState.jsonDeserialize(it) }

        request = request.newBuilder()
            .addHeader(HttpHeaders.ContentType, "application/json")
            .addHeader(HttpHeaders.Authorization,  "Bearer ${authState?.accessToken}")
            .addHeader("trakt-api-version", "2")
            .addHeader("trakt-api-key", clientId)
            .build()
        return chain.proceed(request)
    }
}
