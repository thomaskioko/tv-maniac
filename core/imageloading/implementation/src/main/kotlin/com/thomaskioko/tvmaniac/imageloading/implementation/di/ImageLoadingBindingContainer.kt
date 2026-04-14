package com.thomaskioko.tvmaniac.imageloading.implementation.di

import android.content.Context
import android.os.Looper
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@BindingContainer
@ContributesTo(AppScope::class)
public object ImageLoadingBindingContainer {

    private const val HTTP_RESPONSE_CACHE = (10 * 1024 * 1024).toLong()
    private const val HTTP_TIMEOUT_S = 30

    @Provides
    @SingleIn(AppScope::class)
    public fun provideCache(
        @ApplicationContext context: Context,
    ): Cache {
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
