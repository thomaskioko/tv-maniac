package com.thomaskioko.tvmaniac.data.library.implementation

import com.thomaskioko.tvmaniac.data.library.LibraryDao
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.data.library.model.WatchProvider
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.db.LibraryShows
import com.thomaskioko.tvmaniac.db.WatchProvidersForShow
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultLibraryRepository(
    private val libraryDao: LibraryDao,
    private val datastoreRepository: DatastoreRepository,
    private val formatterUtil: FormatterUtil,
) : LibraryRepository {

    override fun observeLibrary(
        query: String,
        sortOption: LibrarySortOption,
        followedOnly: Boolean,
    ): Flow<List<LibraryItem>> {
        return if (query.isBlank()) {
            libraryDao.observeLibrary(followedOnly = followedOnly)
        } else {
            libraryDao.searchLibrary(query)
        }
            .distinctUntilChanged()
            .map { shows ->
                shows.map { show ->
                    val providers = libraryDao.getWatchProviders(show.show_tmdb_id.id)
                        .map { it.toWatchProvider() }
                    show.toLibraryItem(providers)
                }
            }
            .map { items -> items.applySorting(sortOption) }
    }

    private fun List<LibraryItem>.applySorting(sortOption: LibrarySortOption): List<LibraryItem> {
        return when (sortOption) {
            LibrarySortOption.LAST_WATCHED_DESC ->
                sortedByDescending { it.lastWatchedAt ?: it.followedAt ?: 0L }
            LibrarySortOption.LAST_WATCHED_ASC ->
                sortedBy { it.lastWatchedAt ?: it.followedAt ?: Long.MAX_VALUE }
            LibrarySortOption.NEW_EPISODES ->
                sortedByDescending { it.totalCount - it.watchedCount }
            LibrarySortOption.EPISODES_LEFT_DESC ->
                sortedByDescending { it.totalCount - it.watchedCount }
            LibrarySortOption.EPISODES_LEFT_ASC ->
                sortedBy { it.totalCount - it.watchedCount }
            LibrarySortOption.ALPHABETICAL ->
                sortedBy { it.title.lowercase() }
        }
    }

    override fun observeListStyle(): Flow<Boolean> {
        return datastoreRepository.observeListStyle().map { listStyle ->
            listStyle == ListStyle.GRID
        }
    }

    override suspend fun saveListStyle(isGridMode: Boolean) {
        val listStyle = if (isGridMode) ListStyle.GRID else ListStyle.LIST
        datastoreRepository.saveListStyle(listStyle)
    }

    override fun observeSortOption(): Flow<LibrarySortOption> {
        return datastoreRepository.observeLibrarySortOption().map { sortOptionName ->
            LibrarySortOption.entries.find { it.name == sortOptionName }
                ?: LibrarySortOption.LAST_WATCHED_DESC
        }
    }

    override suspend fun saveSortOption(sortOption: LibrarySortOption) {
        datastoreRepository.saveLibrarySortOption(sortOption.name)
    }

    private fun LibraryShows.toLibraryItem(watchProviders: List<WatchProvider>): LibraryItem =
        LibraryItem(
            traktId = show_trakt_id.id,
            tmdbId = show_tmdb_id.id,
            title = title,
            posterPath = poster_path,
            status = status,
            year = year,
            rating = ratings,
            genres = genres,
            seasonCount = season_count,
            episodeCount = episode_count,
            watchedCount = watched_count,
            totalCount = total_count,
            lastWatchedAt = last_watched_at,
            followedAt = followed_at,
            isFollowed = is_followed == 1L,
            watchProviders = watchProviders,
        )

    private fun WatchProvidersForShow.toWatchProvider(): WatchProvider = WatchProvider(
        id = provider_id.id,
        name = name,
        logoPath = logo_path?.let { formatterUtil.formatTmdbPosterPath(it) },
    )
}
