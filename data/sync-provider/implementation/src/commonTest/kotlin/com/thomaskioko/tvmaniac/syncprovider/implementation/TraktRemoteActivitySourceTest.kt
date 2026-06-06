package com.thomaskioko.tvmaniac.syncprovider.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeActivities
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowActivities
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktSyncRemoteDataSource
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

class TraktRemoteActivitySourceTest {

    private val remoteDataSource = FakeTraktSyncRemoteDataSource()
    private val remoteActivitySource = TraktRemoteActivitySource(remoteDataSource)

    @Test
    fun `should map present timestamps to activity types given successful response`() = runTest {
        remoteDataSource.setLastActivities(
            ApiResponse.Success(
                TraktLastActivitiesResponse(
                    all = "2025-01-04T00:00:00Z",
                    shows = TraktShowActivities(
                        watchlistedAt = "2025-01-01T00:00:00Z",
                        favoritedAt = "2025-01-02T00:00:00Z",
                    ),
                    episodes = TraktEpisodeActivities(
                        watchedAt = "2025-01-03T00:00:00Z",
                        pausedAt = "2025-01-04T00:00:00Z",
                    ),
                ),
            ),
        )

        val result = remoteActivitySource.getLastActivities()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<Map<ActivityType, Instant>>>()
        success.body shouldContainExactly mapOf(
            ActivityType.SHOWS_WATCHLISTED to Instant.parse("2025-01-01T00:00:00Z"),
            ActivityType.SHOWS_FAVORITED to Instant.parse("2025-01-02T00:00:00Z"),
            ActivityType.EPISODES_WATCHED to Instant.parse("2025-01-03T00:00:00Z"),
            ActivityType.EPISODES_PAUSED to Instant.parse("2025-01-04T00:00:00Z"),
        )
    }

    @Test
    fun `should omit absent and unparseable timestamps given partial response`() = runTest {
        remoteDataSource.setLastActivities(
            ApiResponse.Success(
                TraktLastActivitiesResponse(
                    all = "",
                    shows = TraktShowActivities(watchlistedAt = "not-a-timestamp"),
                    episodes = TraktEpisodeActivities(watchedAt = "2025-01-03T00:00:00Z"),
                ),
            ),
        )

        val result = remoteActivitySource.getLastActivities()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<Map<ActivityType, Instant>>>()
        success.body shouldContainExactly mapOf(
            ActivityType.EPISODES_WATCHED to Instant.parse("2025-01-03T00:00:00Z"),
        )
    }

    @Test
    fun `should preserve unauthenticated given provider has no session`() = runTest {
        remoteDataSource.setLastActivities(ApiResponse.Unauthenticated)

        val result = remoteActivitySource.getLastActivities()

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }

    @Test
    fun `should preserve http error given remote failure`() = runTest {
        remoteDataSource.setLastActivities(
            ApiResponse.Error.HttpError(code = 500, errorBody = "boom", errorMessage = "server error"),
        )

        val result = remoteActivitySource.getLastActivities()

        val error = result.shouldBeInstanceOf<ApiResponse.Error.HttpError<Map<ActivityType, Instant>>>()
        error.code shouldBe 500
    }
}
