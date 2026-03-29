package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class FollowShowInteractor(
    private val followedShowsRepository: FollowedShowsRepository,
    private val showContentSyncInteractor: ShowContentSyncInteractor,
    private val upNextRepository: UpNextRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<FollowShowInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            followedShowsRepository.addFollowedShow(params.traktId)

            try {
                showContentSyncInteractor.executeSync(
                    ShowContentSyncInteractor.Param(
                        traktId = params.traktId,
                        forceRefresh = params.forceRefresh,
                    ),
                )
            } catch (t: Throwable) {
                logger.error("FollowShowInteractor", "Failed to sync content for show ${params.traktId}: ${t.message}")
            }

            try {
                upNextRepository.updateUpNextForShow(
                    showTraktId = params.traktId,
                    forceRefresh = params.forceRefresh,
                )
            } catch (t: Throwable) {
                logger.error("FollowShowInteractor", "Failed to update up next for show ${params.traktId}: ${t.message}")
            }
        }
    }

    public data class Param(
        val traktId: Long,
        val forceRefresh: Boolean = false,
    )
}
