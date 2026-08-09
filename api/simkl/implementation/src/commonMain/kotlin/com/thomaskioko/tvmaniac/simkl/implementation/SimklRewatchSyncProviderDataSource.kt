package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWrite
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWriteResult
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSyncProviderDataSource
import com.thomaskioko.tvmaniac.simkl.api.SimklRewatchRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.SimklUserRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAccountTier
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryEpisode
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistorySeason
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryShow
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchShow
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklRewatchSyncProviderDataSource(
    private val rewatchRemoteDataSource: SimklRewatchRemoteDataSource,
    private val userRemoteDataSource: SimklUserRemoteDataSource,
) : RewatchSyncProviderDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL

    private var cachedTier: SimklAccountTier? = null

    override suspend fun supportsRewatch(): Boolean {
        val response = currentTier()
        return response is ApiResponse.Success && response.body == SimklAccountTier.PREMIUM
    }

    override suspend fun readRewatchSessions(providerShowId: Long): ApiResponse<List<RemoteRewatchSession>> =
        rewatchRemoteDataSource.getRewatchSessions().map { response ->
            response.shows
                .filter { it.isRewatch && it.show.ids.tmdb?.toLongOrNull() == providerShowId }
                .map { it.toRemoteRewatchSession() }
        }

    override suspend fun writeRewatch(write: RemoteRewatchWrite): ApiResponse<RemoteRewatchWriteResult> {
        val tierResponse = currentTier()
        if (tierResponse !is ApiResponse.Success) return tierResponse.map { RemoteRewatchWriteResult() }
        if (tierResponse.body != SimklAccountTier.PREMIUM) return ApiResponse.Success(RemoteRewatchWriteResult(skipped = true))

        val request = SimklRewatchHistoryRequest(
            shows = listOf(
                SimklRewatchHistoryShow(
                    ids = SimklShowIds(tmdb = write.providerShowId.toString()),
                    rewatchId = write.providerSessionId,
                    seasons = listOf(
                        SimklRewatchHistorySeason(
                            number = write.seasonNumber.toInt(),
                            episodes = listOf(
                                SimklRewatchHistoryEpisode(
                                    number = write.episodeNumber.toInt(),
                                    watchedAt = Instant.fromEpochMilliseconds(write.watchedAt).toString(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        return rewatchRemoteDataSource.addRewatchHistory(request).map { response ->
            RemoteRewatchWriteResult(providerSessionId = response.added?.statuses?.firstOrNull()?.response?.rewatchId)
        }
    }

    private suspend fun currentTier(): ApiResponse<SimklAccountTier> {
        cachedTier?.let { return ApiResponse.Success(it) }
        return userRemoteDataSource.getUserSettings()
            .map { SimklAccountTier.fromRaw(it.account.type) }
            .also { response -> if (response is ApiResponse.Success) cachedTier = response.body }
    }
}

private fun SimklRewatchShow.toRemoteRewatchSession(): RemoteRewatchSession = RemoteRewatchSession(
    providerSessionId = rewatchId,
    seasonNumber = null,
    episodeNumber = null,
    episodeCount = watchedEpisodesCount?.toLong()?.takeIf { it > 0 },
    lastWatchedAt = lastWatchedAt?.let { runCatching { Instant.parse(it).toEpochMilliseconds() }.getOrNull() },
)
