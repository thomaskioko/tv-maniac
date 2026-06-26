package com.thomaskioko.tvmaniac.imageloading.api

import kotlinx.coroutines.flow.Flow

public interface ImageQualityPreference {
    public fun observeImageQuality(): Flow<String>
}
