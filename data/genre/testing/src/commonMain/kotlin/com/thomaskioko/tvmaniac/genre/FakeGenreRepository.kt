package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.db.Tvshow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeGenreRepository : GenreRepository {
    private var entityListResult = MutableStateFlow<List<ShowGenresEntity>>(emptyList())
    private var showListResult = MutableStateFlow<List<Tvshow>>(emptyList())

    public suspend fun setGenreResult(result: List<ShowGenresEntity>) {
        entityListResult.emit(result)
    }

    public suspend fun setShowResult(result: List<Tvshow>) {
        showListResult.emit(result)
    }

    override suspend fun fetchGenresWithShows(forceRefresh: Boolean) {
    }

    override suspend fun fetchShowByGenreId(id: String, forceRefresh: Boolean) {
    }

    override fun observeGenresWithShows(): Flow<List<ShowGenresEntity>> = entityListResult.asStateFlow()

    override suspend fun observeShowByGenreId(id: String): Flow<List<Tvshow>> = showListResult.asStateFlow()

    override suspend fun observeGenrePosters() {
    }
}
