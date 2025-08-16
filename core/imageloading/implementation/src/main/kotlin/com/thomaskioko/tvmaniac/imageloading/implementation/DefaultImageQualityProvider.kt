package com.thomaskioko.tvmaniac.imageloading.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.imageloading.api.ImageQualityProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultImageQualityProvider(
    coroutineScope: AppCoroutineScope,
    private val datastoreRepository: DatastoreRepository,
) : ImageQualityProvider {

    private val currentQuality = MutableStateFlow(ImageQuality.HIGH)

    init {
        coroutineScope.io.launch {
            datastoreRepository.observeImageQuality()
                .collectLatest { quality ->
                    currentQuality.value = quality
                }
        }
    }

    override fun getCurrentQuality(): ImageQuality = currentQuality.value

    override fun observeQuality(): Flow<ImageQuality> = currentQuality.asStateFlow()
}
