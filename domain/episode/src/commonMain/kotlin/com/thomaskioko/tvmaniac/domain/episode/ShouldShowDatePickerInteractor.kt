package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

@Inject
public class ShouldShowDatePickerInteractor(
    private val datastoreRepository: DatastoreRepository,
) {
    public suspend operator fun invoke(): Boolean =
        datastoreRepository.observeCustomWatchDateEnabled().first()
}
