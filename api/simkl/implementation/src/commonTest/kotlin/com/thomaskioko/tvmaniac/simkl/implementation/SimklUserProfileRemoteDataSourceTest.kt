package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAccount
import com.thomaskioko.tvmaniac.simkl.api.model.SimklStatsDomain
import com.thomaskioko.tvmaniac.simkl.api.model.SimklStatusBucket
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUser
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserSettingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserStatsResponse
import com.thomaskioko.tvmaniac.simkl.testing.FakeSimklUserRemoteDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SimklUserProfileRemoteDataSourceTest {

    private val fakeRemoteDataSource = FakeSimklUserRemoteDataSource(
        userSettingsResponse = ApiResponse.Success(
            SimklUserSettingsResponse(
                user = SimklUser(name = "testuser", avatar = null, bio = null, gender = null),
                account = SimklAccount(id = 12345678L),
            ),
        ),
    )

    private val dataSource = SimklUserProfileRemoteDataSource(remoteDataSource = fakeRemoteDataSource)

    @Test
    fun `should sum tv and anime watched shows across buckets given getUserStats returns stats`() = runTest {
        fakeRemoteDataSource.setUserStats(
            ApiResponse.Success(
                SimklUserStatsResponse(
                    totalMins = 97200,
                    tv = SimklStatsDomain(
                        watching = SimklStatusBucket(count = 5, watchedEpisodesCount = 120),
                        completed = SimklStatusBucket(count = 30, watchedEpisodesCount = 800),
                        hold = SimklStatusBucket(count = 2, watchedEpisodesCount = 40),
                        plantowatch = SimklStatusBucket(count = 15, watchedEpisodesCount = 0),
                    ),
                    anime = SimklStatsDomain(
                        watching = SimklStatusBucket(count = 3, watchedEpisodesCount = 48),
                        completed = SimklStatusBucket(count = 12, watchedEpisodesCount = 288),
                        hold = SimklStatusBucket(count = 1, watchedEpisodesCount = 10),
                        plantowatch = SimklStatusBucket(count = 8, watchedEpisodesCount = 0),
                    ),
                ),
            ),
        )

        val result = dataSource.getUserStats(userId = "12345678")

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteUserStats?>>()
        val stats = success.body
        stats shouldBe RemoteUserStats(
            showsWatched = 53L,
            episodesWatched = 1306L,
            minutesWatched = 97200L,
        )
    }

    @Test
    fun `should exclude plantowatch from show and episode counts given getUserStats returns stats`() = runTest {
        fakeRemoteDataSource.setUserStats(
            ApiResponse.Success(
                SimklUserStatsResponse(
                    totalMins = 5000,
                    tv = SimklStatsDomain(
                        watching = null,
                        completed = SimklStatusBucket(count = 10, watchedEpisodesCount = 200),
                        hold = null,
                        plantowatch = SimklStatusBucket(count = 100, watchedEpisodesCount = 0),
                    ),
                    anime = null,
                ),
            ),
        )

        val result = dataSource.getUserStats(userId = "12345678")

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteUserStats?>>()
        val stats = success.body
        stats?.showsWatched shouldBe 10L
        stats?.episodesWatched shouldBe 200L
        stats?.minutesWatched shouldBe 5000L
    }

    @Test
    fun `should return null stats given userId is not a valid simkl account id`() = runTest {
        val result = dataSource.getUserStats(userId = "not-a-number")

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteUserStats?>>()
        success.body shouldBe null
    }

    @Test
    fun `should use zero minutes given totalMins is absent from stats response`() = runTest {
        fakeRemoteDataSource.setUserStats(
            ApiResponse.Success(
                SimklUserStatsResponse(
                    totalMins = null,
                    tv = SimklStatsDomain(
                        completed = SimklStatusBucket(count = 5, watchedEpisodesCount = 50),
                    ),
                    anime = null,
                ),
            ),
        )

        val result = dataSource.getUserStats(userId = "12345678")

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteUserStats?>>()
        success.body?.minutesWatched shouldBe 0L
    }
}
