package com.thomaskioko.tvmaniac.followedshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFollowedShowsRepository(
    private val followedShowsDao: FollowedShowsDao,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : FollowedShowsRepository {

    override suspend fun getFollowedShows(): List<FollowedShowEntry> =
        withContext(dispatchers.io) { followedShowsDao.entries() }

    override suspend fun addFollowedShow(traktId: Long) {
        withContext(dispatchers.io) {
            transactionRunner {
                val existingEntry = followedShowsDao.entryWithTraktId(traktId)
                if (existingEntry == null || existingEntry.pendingAction == PendingAction.DELETE) {
                    val _ = followedShowsDao.upsert(
                        FollowedShowEntry(
                            id = existingEntry?.id ?: 0,
                            traktId = traktId,
                            tmdbId = existingEntry?.tmdbId,
                            followedAt = dateTimeProvider.now(),
                            pendingAction = PendingAction.UPLOAD,
                        ),
                    )
                    logger.debug(TAG, "Marked show $traktId for upload")
                }
            }
        }
    }

    override suspend fun removeFollowedShow(traktId: Long) {
        withContext(dispatchers.io) {
            transactionRunner {
                followedShowsDao.entryWithTraktId(traktId)?.also { entry ->
                    if (entry.pendingAction == PendingAction.UPLOAD) {
                        followedShowsDao.deleteById(entry.id)
                        logger.debug(TAG, "Deleted local-only show $traktId")
                    } else {
                        val _ = followedShowsDao.upsert(entry.copy(pendingAction = PendingAction.DELETE))
                        logger.debug(TAG, "Marked show $traktId for deletion")
                    }
                }
            }
        }
    }

    private companion object {
        private const val TAG = "FollowedShowsRepository"
    }
}
