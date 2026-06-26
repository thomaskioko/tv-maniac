package com.thomaskioko.tvmaniac.datastore.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.imageloading.api.ImageQualityPreference
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DatastoreImageQualityPreference(
    private val datastoreRepository: DatastoreRepository,
) : ImageQualityPreference {
    override fun observeImageQuality(): Flow<String> =
        datastoreRepository.observeImageQuality().map { it.name }
}
