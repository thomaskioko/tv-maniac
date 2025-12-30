package com.thomaskioko.tvmaniac.imageloading.implementation

import android.content.Context
import android.os.Build
import androidx.compose.animation.core.AnimationConstants
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.imageloading.api.ImageQualityProvider
import com.thomaskioko.tvmaniac.imageloading.implementation.interceptors.TmdbInterceptor
import me.tatarka.inject.annotations.Inject
import okhttp3.OkHttpClient
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class CoilImageLoaderFactory(
    private val context: Context,
    private val okHttpClient: Lazy<OkHttpClient>,
    private val imageQualityProvider: ImageQualityProvider,
    private val tmdbInterceptor: TmdbInterceptor,
) {
    public fun create(): ImageLoader {
        return ImageLoader.Builder(context)
            .callFactory { request -> okHttpClient.value.newCall(request) }
            .components {
                add(tmdbInterceptor)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                }
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(calculateDiskCacheSize())
                    .build()
            }
            .crossfade(AnimationConstants.DefaultDurationMillis)
            .respectCacheHeaders(false)
            .build()
    }

    private fun calculateDiskCacheSize(): Long {
        val baseSize = 250L * 1024 * 1024 // 250MB
        return when (imageQualityProvider.getCurrentQuality()) {
            ImageQuality.LOW -> baseSize / 2 // 125MB for low quality
            ImageQuality.MEDIUM -> baseSize // 250MB for medium
            ImageQuality.HIGH -> baseSize * 2 // 500MB for high quality
        }
    }
}
