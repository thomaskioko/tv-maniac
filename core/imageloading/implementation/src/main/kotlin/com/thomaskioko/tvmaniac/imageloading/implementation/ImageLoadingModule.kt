package com.thomaskioko.tvmaniac.imageloading.implementation

import android.content.Context
import android.os.Looper
import me.tatarka.inject.annotations.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.util.concurrent.TimeUnit

@ContributesTo(AppScope::class)
public interface ImageLoadingModule {

    public companion object {
        private const val HTTP_RESPONSE_CACHE = (10 * 1024 * 1024).toLong()
        private const val HTTP_TIMEOUT_S = 30
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideCache(context: Context): Cache {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("Cache initialized on main thread.")
        }
        return Cache(context.cacheDir, HTTP_RESPONSE_CACHE)
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideOkHttpClient(
        cache: Cache,
        interceptors: Set<Interceptor> = emptySet(),
    ): OkHttpClient {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("HTTP client initialized on main thread.")
        }

        val builder = OkHttpClient.Builder()
            .connectTimeout(HTTP_TIMEOUT_S.toLong(), TimeUnit.SECONDS)
            .readTimeout(HTTP_TIMEOUT_S.toLong(), TimeUnit.SECONDS)
            .writeTimeout(HTTP_TIMEOUT_S.toLong(), TimeUnit.SECONDS)
            .cache(cache)

        builder.networkInterceptors().addAll(interceptors)
        builder.interceptors().addAll(interceptors)

        return builder.build()
    }
}
