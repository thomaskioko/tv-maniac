package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch

public class FakeEpisodeWatchesDataSource : EpisodeWatchesDataSource {
    override var provider: ConnectedProvider = ConnectedProvider.TRAKT
    private val watchesMap = mutableMapOf<Long, List<WatchedEpisodeEntry>>()
    private val allWatchedShowsPages = mutableMapOf<Int, List<WatchedShowBatch>>()
    private val addEpisodeWatchesInvocations = mutableListOf<List<WatchedEpisodeEntry>>()
    private val removeEpisodeWatchesInvocations = mutableListOf<List<WatchedEpisodeEntry>>()
    private var allWatchedShowsError: Throwable? = null

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
