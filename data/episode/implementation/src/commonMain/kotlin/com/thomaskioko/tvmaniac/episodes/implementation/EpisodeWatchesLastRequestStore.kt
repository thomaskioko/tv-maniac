package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration

@Inject
@SingleIn(AppScope::class)
public class EpisodeWatchesLastRequestStore(
    private val requestManagerRepository: RequestManagerRepository,
) {

    public fun isRequestValid(threshold: Duration = DEFAULT_EXPIRY): Boolean =
        requestManagerRepository.isRequestValid(REQUEST_TYPE, threshold)

    public fun updateLastRequest() {
        requestManagerRepository.upsert(ENTITY_ID, REQUEST_TYPE)
    }

    public fun isRequestExpired(expiry: Duration = DEFAULT_EXPIRY): Boolean =
        !isRequestValid(expiry)

    public fun isShowRequestExpired(showTraktId: Long, expiry: Duration = SHOW_DEFAULT_EXPIRY): Boolean =
        requestManagerRepository.isRequestExpired(showTraktId, SHOW_REQUEST_TYPE, expiry)

    public fun updateShowLastRequest(showTraktId: Long) {
        requestManagerRepository.upsert(showTraktId, SHOW_REQUEST_TYPE)
    }

    private companion object {
        const val SHOW_REQUEST_TYPE = "SHOW_EPISODE_WATCHES_SYNC"
        val SHOW_DEFAULT_EXPIRY: Duration = RequestTypeConfig.SHOW_EPISODE_WATCHES_SYNC.duration
        val REQUEST_TYPE = RequestTypeConfig.EPISODE_WATCHES_SYNC.name
        val ENTITY_ID = RequestTypeConfig.EPISODE_WATCHES_SYNC.requestId
        val DEFAULT_EXPIRY: Duration = RequestTypeConfig.EPISODE_WATCHES_SYNC.duration
    }
}
