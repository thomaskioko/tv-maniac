package com.thomaskioko.tvmaniac.imageloading.implementation.interceptors

import coil.intercept.Interceptor
import coil.request.ImageResult
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.imageloading.api.ImageQualityProvider
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class TmdbInterceptor(
    private val imageQualityProvider: ImageQualityProvider,
) : Interceptor {

    public companion object Companion {
        private const val SIZE_W185 = "w185"
        private const val SIZE_W500 = "w500"
        private const val SIZE_W780 = "w780"
        private const val SIZE_ORIGINAL = "original"

        private const val SIZE_W300 = "w300"

        private val POSTER_SIZES = mapOf(
            ImageQuality.LOW to SIZE_W185,
            ImageQuality.MEDIUM to SIZE_W500,
            ImageQuality.HIGH to SIZE_ORIGINAL,
        )

        private val BACKDROP_SIZES = mapOf(
            ImageQuality.LOW to SIZE_W300,
            ImageQuality.MEDIUM to SIZE_W780,
            ImageQuality.HIGH to SIZE_ORIGINAL,
        )
    }

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val data = request.data

        if (data is String && data.contains("image.tmdb.org")) {
            val quality = imageQualityProvider.getCurrentQuality()
            val optimizedUrl = optimizeTmdbUrl(
                url = data,
                quality = quality,
                imageType = detectImageType(data),
            )

            val cacheKey = generateQualityCacheKey(data, quality)

            return chain.proceed(
                request.newBuilder()
                    .data(optimizedUrl)
                    .memoryCacheKey(cacheKey)
                    .diskCacheKey(cacheKey)
                    .build(),
            )
        }

        return chain.proceed(request)
    }

    private fun optimizeTmdbUrl(
        url: String,
        quality: ImageQuality,
        imageType: ImageType,
    ): String {
        val size = when (imageType) {
            ImageType.POSTER, ImageType.PROFILE -> POSTER_SIZES[quality]
            ImageType.BACKDROP -> BACKDROP_SIZES[quality]
        } ?: SIZE_W500

        return url
            .replace(Regex("/w\\d+/"), "/$size/")
            .replace("/original/", "/$size/")
    }

    private fun detectImageType(url: String): ImageType {
        return when {
            url.contains("/poster/") -> ImageType.POSTER
            url.contains("/backdrop/") -> ImageType.BACKDROP
            url.contains("/profile/") -> ImageType.PROFILE
            else -> ImageType.POSTER
        }
    }

    private fun generateQualityCacheKey(baseUrl: String, quality: ImageQuality): String {
        val cleanUrl = baseUrl.substringBefore("?")
        return "${cleanUrl}_${quality.name.lowercase()}"
    }

    private enum class ImageType {
        POSTER,
        BACKDROP,
        PROFILE,
    }
}
