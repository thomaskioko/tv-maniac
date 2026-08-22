package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.ShowRefillReporter
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.shows.api.ShowTraktIdResolver
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

@Inject
public class SyncRestoredShowsInteractor(
    private val backupRepository: BackupRepository,
    private val syncShowMetadataInteractor: SyncShowMetadataInteractor,
    private val traktIdResolver: ShowTraktIdResolver,
    private val refillReporter: ShowRefillReporter,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<Unit>() {

    override suspend fun doWork(params: Unit) {
        val showIds = backupRepository.showsNeedingMetadata()
        if (showIds.isEmpty()) {
            logger.debug(TAG, "No restored shows need metadata")
            refillReporter.clear()
            return
        }

        logger.debug(TAG, "Refilling metadata for ${showIds.size} restored shows")
        refillReporter.begin(showIds.size)

        withContext(dispatchers.io) {
            resolveTraktIds(showIds)

            for (showId in showIds) {
                currentCoroutineContext().ensureActive()

                val failure = runCatching {
                    syncShowMetadataInteractor.executeSync(
                        SyncShowMetadataInteractor.Param(showId = showId, forceRefresh = true),
                    )
                }.exceptionOrNull()

                if (failure == null) {
                    refillReporter.advance()
                    continue
                }
                if (failure is CancellationException) throw failure

                logger.warning(TAG, "Metadata refill failed for $showId: ${failure.message}")
                if (failure.toSyncError() is SyncError.Retryable) {
                    logger.warning(TAG, "Stopping metadata refill after a retryable failure on $showId")
                    return@withContext
                }
                refillReporter.advance()
            }
        }

        refillReporter.clear()
    }

    private suspend fun resolveTraktIds(showIds: List<Long>) {
        val resolved = runCatching { traktIdResolver.resolveMissingTraktIds(showIds) }
            .onFailure { logger.warning(TAG, "Trakt id resolution failed: ${it.message}") }
            .getOrNull()
            ?: return
        if (resolved > 0) logger.debug(TAG, "Resolved $resolved trakt ids for restored shows")
    }

    private companion object {
        private const val TAG = "SyncRestoredShows"
    }
}
