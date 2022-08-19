package com.thomaskioko.tvmaniac.tmdb.implementation

import io.ktor.http.HttpHeaders
import okhttp3.Interceptor
import okhttp3.Response


class TmdbInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val urlBuilder = request.url.newBuilder()
            .scheme("https")
            .host("api.themoviedb.org")
            .addQueryParameter("api_key", BuildKonfig.TMDB_API_KEY)
            .build()


        request = request.newBuilder()
            .addHeader(HttpHeaders.ContentType, "application/json")
            .addHeader(HttpHeaders.CacheControl, "public, max-age=" + 60 * 5)
            .url(urlBuilder)
            .build()
        return chain.proceed(request)
    }
}