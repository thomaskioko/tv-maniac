package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.backup.api.BackupList
import com.thomaskioko.tvmaniac.data.backup.api.RestoredListWriter
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.shows.api.ShowTraktIdResolver
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.first

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class TraktRestoredListWriter(
    private val traktListRepository: TraktListRepository,
    private val traktListDao: TraktListDao,
    private val userRepository: UserRepository,
    private val traktIdResolver: ShowTraktIdResolver,
    private val logger: Logger,
) : RestoredListWriter {

    override suspend fun restoreLists(lists: List<BackupList>): Int {
        if (lists.isEmpty()) return 0

        val slug = userRepository.getCurrentUser()?.slug
        if (slug == null) {
            logger.debug(TAG, "No signed in user, leaving ${lists.size} lists unrestored")
            return 0
        }

        traktIdResolver.resolveMissingTraktIds(lists.flatMap { list -> list.shows.map { it.tmdbId } }.distinct())

        var restored = 0
        for (list in lists) {
            val listId = existingListId(list.name) ?: createList(slug, list.name)
            if (listId == null) {
                logger.warning(TAG, "Could not create list ${list.name}")
                continue
            }
            addMembers(slug = slug, listId = listId, list = list)
            restored++
        }
        return restored
    }

    private suspend fun existingListId(name: String): Long? =
        traktListDao.observeAll().first().firstOrNull { it.name == name }?.id

    private suspend fun createList(slug: String, name: String): Long? {
        val created = runCatching { traktListRepository.createList(slug = slug, name = name) }
        if (created.isFailure) {
            logger.warning(TAG, "Creating list $name failed: ${created.exceptionOrNull()?.message}")
            return null
        }
        return existingListId(name)
    }

    private suspend fun addMembers(slug: String, listId: Long, list: BackupList) {
        list.shows.forEach { show ->
            runCatching {
                traktListRepository.toggleShowInList(
                    slug = slug,
                    listId = listId,
                    showId = show.tmdbId,
                    isCurrentlyInList = false,
                )
            }.onFailure {
                logger.warning(TAG, "Adding ${show.tmdbId} to ${list.name} failed: ${it.message}")
            }
        }
    }

    private companion object {
        private const val TAG = "TraktRestoredListWriter"
    }
}
