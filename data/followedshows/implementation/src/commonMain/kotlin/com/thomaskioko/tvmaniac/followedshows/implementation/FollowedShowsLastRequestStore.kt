package com.thomaskioko.tvmaniac.followedshows.implementation

import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

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
        private const val REQUEST_TYPE = "FOLLOWED_SHOWS_SYNC"
        private const val ENTITY_ID = 0L
        public val DEFAULT_EXPIRY: Duration = 3.hours
    }
}
