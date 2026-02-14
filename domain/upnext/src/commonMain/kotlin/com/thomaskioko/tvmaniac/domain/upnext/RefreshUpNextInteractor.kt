package com.thomaskioko.tvmaniac.domain.upnext

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import me.tatarka.inject.annotations.Inject

@Inject
public class RefreshUpNextInteractor(
    private val upNextRepository: UpNextRepository,
    private val datastoreRepository: DatastoreRepository,
    private val dateTimeProvider: DateTimeProvider,
) : Interactor<Boolean>() {

    override suspend fun doWork(params: Boolean) {
        upNextRepository.fetchUpNextEpisodes(forceRefresh = params)
        datastoreRepository.setLastUpNextSyncTimestamp(dateTimeProvider.nowMillis())
    }
}
