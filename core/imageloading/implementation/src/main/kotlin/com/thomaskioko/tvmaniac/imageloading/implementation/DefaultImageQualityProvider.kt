package com.thomaskioko.tvmaniac.imageloading.implementation

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.imageloading.api.ImageQualityProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultImageQualityProvider(
    @IoCoroutineScope coroutineScope: CoroutineScope,
    private val datastoreRepository: DatastoreRepository,
) : ImageQualityProvider {

    private val currentQuality = MutableStateFlow(ImageQuality.AUTO)

    init {
        coroutineScope.launch {
            datastoreRepository.observeImageQuality()
                .collectLatest { quality ->
                    currentQuality.value = quality
                }
        }
    }

    override fun getCurrentQuality(): ImageQuality = currentQuality.value

    override fun observeQuality(): Flow<ImageQuality> = currentQuality.asStateFlow()
}
