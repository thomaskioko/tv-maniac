package com.thomaskioko.tvmaniac.followedshows.implementation

import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration

@Inject
@SingleIn(AppScope::class)
public class FollowedShowsLastRequestStore(
    private val requestManagerRepository: RequestManagerRepository,
) {

    public fun isRequestValid(threshold: Duration = DEFAULT_EXPIRY): Boolean =
        requestManagerRepository.isRequestValid(REQUEST_TYPE, threshold)

    public fun updateLastRequest() {
        requestManagerRepository.upsert(ENTITY_ID, REQUEST_TYPE)
    }

    public fun isRequestExpired(expiry: Duration = DEFAULT_EXPIRY): Boolean =
        !isRequestValid(expiry)

    public companion object {
        private val REQUEST_TYPE = RequestTypeConfig.FOLLOWED_SHOWS_SYNC.name
        private val ENTITY_ID = RequestTypeConfig.FOLLOWED_SHOWS_SYNC.requestId
        public val DEFAULT_EXPIRY: Duration = RequestTypeConfig.FOLLOWED_SHOWS_SYNC.duration
    }
}
