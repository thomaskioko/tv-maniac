package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.ShowRuntime
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch

public class FakeEpisodeWatchesDataSource : EpisodeWatchesDataSource {
    override var provider: SyncProviderSource = SyncProviderSource.TRAKT
    private val watchesMap = mutableMapOf<Long, List<WatchedEpisodeEntry>>()
    private val allWatchedShowsPages = mutableMapOf<Int, List<WatchedShowBatch>>()
    private val addEpisodeWatchesInvocations = mutableListOf<List<WatchedEpisodeEntry>>()
    private val removeEpisodeWatchesInvocations = mutableListOf<List<WatchedEpisodeEntry>>()
    private var allWatchedShowsError: Throwable? = null
    private val showRuntimePages = mutableMapOf<Int, List<ShowRuntime>>()
    private val runtimeRequestPages = mutableListOf<Int>()

    public fun setShowRuntimePage(page: Int, runtimes: List<ShowRuntime>) {
        showRuntimePages[page] = runtimes
    }

    public fun runtimeRequestPages(): List<Int> = runtimeRequestPages.toList()

    override suspend fun getWatchedShowRuntimes(page: Int, limit: Int): List<ShowRuntime> {
        runtimeRequestPages.add(page)
        return showRuntimePages[page].orEmpty()
    }

    public fun setShowEpisodeWatches(showId: Long, watches: List<WatchedEpisodeEntry>) {
        watchesMap[showId] = watches
    }

    public fun setAllWatchedShowsPage(page: Int, batches: List<WatchedShowBatch>) {
        allWatchedShowsPages[page] = batches
    }

    public fun setAllWatchedShowsError(error: Throwable?) {
        allWatchedShowsError = error
    }

    public fun addEpisodeWatchesInvocations(): List<List<WatchedEpisodeEntry>> =
        addEpisodeWatchesInvocations.toList()

    public fun removeEpisodeWatchesInvocations(): List<List<WatchedEpisodeEntry>> =
        removeEpisodeWatchesInvocations.toList()

    override suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry> {
        return watchesMap[showId] ?: emptyList()
    }

    override suspend fun getAllWatchedShows(page: Int, limit: Int): List<WatchedShowBatch> {
        allWatchedShowsError?.let { throw it }
        return allWatchedShowsPages[page] ?: emptyList()
    }

    override suspend fun addEpisodeEntries(entries: List<WatchedEpisodeEntry>) {
        addEpisodeWatchesInvocations.add(entries)
    }

    override suspend fun removeEpisodeEntries(entries: List<WatchedEpisodeEntry>) {
        removeEpisodeWatchesInvocations.add(entries)
    }
}
