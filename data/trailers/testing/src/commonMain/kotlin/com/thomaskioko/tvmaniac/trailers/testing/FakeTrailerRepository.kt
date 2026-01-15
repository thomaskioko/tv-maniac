package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

public class FakeTrailerRepository : TrailerRepository {
    private val youtubePlayerInstalled = Channel<Boolean>(Channel.UNLIMITED)
    private var response = MutableStateFlow<List<SelectByShowTraktId>>(emptyList())

    public suspend fun setTrailerResult(result: List<SelectByShowTraktId>) {
        response.emit(result)
    }

    public suspend fun setYoutubePlayerInstalled(installed: Boolean) {
        youtubePlayerInstalled.send(installed)
    }

    override fun observeTrailers(traktId: Long): Flow<List<SelectByShowTraktId>> = response.asStateFlow()

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = youtubePlayerInstalled.receiveAsFlow()

    override suspend fun fetchTrailers(traktId: Long, forceRefresh: Boolean) {
    }
}
