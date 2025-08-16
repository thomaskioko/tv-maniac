package com.thomaskioko.tvmaniac.imageloading.api

import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import kotlinx.coroutines.flow.Flow

interface ImageQualityProvider {
    fun getCurrentQuality(): ImageQuality
    fun observeQuality(): Flow<ImageQuality>
}
