package com.thomaskioko.tvmaniac.domain.settings

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveLayoutPreferencesInteractor(
    private val datastoreRepository: DatastoreRepository,
) : SubjectInteractor<Unit, LayoutPreferences>() {

    override fun createObservable(params: Unit): Flow<LayoutPreferences> =
        datastoreRepository.observeLayoutPreferences()
}
