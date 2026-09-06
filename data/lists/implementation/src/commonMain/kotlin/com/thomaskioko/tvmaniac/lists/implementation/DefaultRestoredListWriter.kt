package com.thomaskioko.tvmaniac.lists.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.backup.api.RestoredListWriter
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupList
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.lists.api.ListDao
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import com.thomaskioko.tvmaniac.shows.api.ShowTraktIdResolver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.first

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultRestoredListWriter(
    private val listRepository: ListRepository,
    private val listDao: ListDao,
    private val userRepository: UserRepository,
    private val activeProviderFeatures: () -> ProviderFeatures,
    private val traktIdResolver: ShowTraktIdResolver,
    private val logger: Logger,
) : RestoredListWriter {

    override suspend fun restoreLists(lists: List<BackupList>): Int {
        if (lists.isEmpty()) return 0

        val traktSlug = if (activeProviderFeatures().supportsLists) userRepository.getCurrentUser()?.slug else null

        traktIdResolver.resolveMissingTraktIds(lists.flatMap { list -> list.shows.map { it.tmdbId } }.distinct())

        var restored = 0
        for (list in lists) {
            val listId = existingListId(list.name) ?: createList(name = list.name, traktSlug = traktSlug)
            if (listId == null) {
                logger.warning(TAG, "Could not create list ${list.name}")
                continue
            }
            addMembers(traktSlug = traktSlug, listId = listId, list = list)
            restored++
        }
        return restored
    }

    private suspend fun existingListId(name: String): Long? =
        listDao.observeAll().first().firstOrNull { it.name == name }?.id

    private suspend fun createList(name: String, traktSlug: String?): Long? {
        val created = runCatching { listRepository.createList(name = name, traktSlug = traktSlug) }
        if (created.isFailure) {
            logger.warning(TAG, "Creating list $name failed: ${created.exceptionOrNull()?.message}")
            return null
        }
        return existingListId(name)
    }

    private suspend fun addMembers(traktSlug: String?, listId: Long, list: BackupList) {
        list.shows.forEach { show ->
            runCatching {
                listRepository.toggleShowInList(
                    listId = listId,
                    showId = show.tmdbId,
                    isCurrentlyInList = false,
                    traktSlug = traktSlug,
                )
            }.onFailure {
                logger.warning(TAG, "Adding ${show.tmdbId} to ${list.name} failed: ${it.message}")
            }
        }
    }

    private companion object {
        private const val TAG = "DefaultRestoredListWriter"
    }
}
