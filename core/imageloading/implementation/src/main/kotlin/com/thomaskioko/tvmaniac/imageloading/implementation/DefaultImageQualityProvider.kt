package com.thomaskioko.tvmaniac.imageloading.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.imageloading.api.ImageQualityProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultImageQualityProvider(
    coroutineScope: AppCoroutineScope,
    private val datastoreRepository: DatastoreRepository,
) : ImageQualityProvider {

    private val currentQuality = MutableStateFlow(ImageQuality.AUTO)

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
