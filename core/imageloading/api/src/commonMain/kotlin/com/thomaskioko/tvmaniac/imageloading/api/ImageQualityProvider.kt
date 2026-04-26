package com.thomaskioko.tvmaniac.imageloading.api

import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import kotlinx.coroutines.flow.Flow

public interface ImageQualityProvider {
    public fun getCurrentQuality(): ImageQuality
    public fun observeQuality(): Flow<ImageQuality>
}
