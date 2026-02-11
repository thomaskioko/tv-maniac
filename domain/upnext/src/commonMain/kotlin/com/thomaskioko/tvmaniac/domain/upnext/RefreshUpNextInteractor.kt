package com.thomaskioko.tvmaniac.domain.upnext

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class RefreshUpNextInteractor(
    private val upNextRepository: UpNextRepository,
) : Interactor<Boolean>() {

    override suspend fun doWork(params: Boolean) {
        upNextRepository.fetchUpNextEpisodes(forceRefresh = params)
    }
}
