package com.thomaskioko.tvmaniac.genre

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeGenreRepository : GenreRepository {
    private var entityListResult = MutableStateFlow<List<ShowGenresEntity>>(emptyList())
    private var showListResult = MutableStateFlow<List<Tvshow>>(emptyList())
    private var genreWithShowsResult = MutableStateFlow<List<GenreWithShowsEntity>>(emptyList())
    private var genreShowCategoryFlow = MutableStateFlow(GenreShowCategory.POPULAR)
    private var genreSlugsResult: List<String> = emptyList()
    private var pagedGenreShowsResult = MutableStateFlow<PagingData<ShowEntity>>(PagingData.empty())

    public fun setPagedGenreShows(pagingData: PagingData<ShowEntity>) {
        pagedGenreShowsResult.value = pagingData
    }

    public suspend fun setGenreResult(result: List<ShowGenresEntity>) {
        entityListResult.emit(result)
    }

    public suspend fun setShowResult(result: List<Tvshow>) {
        showListResult.emit(result)
    }

    public suspend fun setGenreWithShowsResult(result: List<GenreWithShowsEntity>) {
        genreWithShowsResult.emit(result)
    }

    public fun setGenreSlugs(slugs: List<String>) {
        genreSlugsResult = slugs
    }

    public fun setGenreShowCategory(category: GenreShowCategory) {
        genreShowCategoryFlow.value = category
    }

    override suspend fun saveGenreShowCategory(category: GenreShowCategory) {
        genreShowCategoryFlow.value = category
    }

    override suspend fun getGenreShowCategory(): GenreShowCategory = genreShowCategoryFlow.value

    override fun observeGenreShowCategory(): Flow<GenreShowCategory> = genreShowCategoryFlow.asStateFlow()

    override suspend fun fetchGenresWithShows(forceRefresh: Boolean) {
    }

    override suspend fun fetchShowByGenreId(id: String, forceRefresh: Boolean) {
    }

    override fun observeGenresWithShows(): Flow<List<ShowGenresEntity>> = entityListResult.asStateFlow()

    override suspend fun observeShowByGenreId(id: String): Flow<List<Tvshow>> = showListResult.asStateFlow()

    override suspend fun observeGenrePosters() {
    }

    override fun getGenreSlugs(): List<String> = genreSlugsResult

    override suspend fun fetchTraktGenres(forceRefresh: Boolean) {
    }

    override suspend fun fetchGenreShows(slug: String, category: GenreShowCategory, forceRefresh: Boolean) {
    }

    override fun observeGenresWithShowRows(): Flow<List<GenreWithShowsEntity>> = genreWithShowsResult.asStateFlow()

    override fun getPagedGenreShows(
        slug: String,
        category: GenreShowCategory,
        forceRefresh: Boolean,
    ): Flow<PagingData<ShowEntity>> = pagedGenreShowsResult.asStateFlow()
}
