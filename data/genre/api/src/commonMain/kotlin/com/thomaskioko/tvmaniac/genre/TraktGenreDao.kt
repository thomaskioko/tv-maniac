package com.thomaskioko.tvmaniac.genre

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.genre.model.TraktGenreEntity
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface TraktGenreDao {
    public fun upsertGenre(slug: String, name: String)
    public fun observeGenres(): Flow<List<TraktGenreEntity>>
    public fun getGenreSlugs(): List<String>
    public fun deleteAllGenres()
    public fun upsertGenreShow(genreSlug: String, showId: Long, pageOrder: Long, category: String, page: Long)
    public fun observeShowsByGenreSlug(slug: String): Flow<List<ShowEntity>>
    public fun observeShowsByGenreSlugCategoryAndPage(slug: String, category: String, page: Long): Flow<List<ShowEntity>>
    public fun observeGenresWithShowsByCategory(category: String): Flow<List<GenreWithShowsEntity>>
    public fun getPagedShowsByGenreSlugAndCategory(slug: String, category: String): PagingSource<Int, ShowEntity>
    public fun pageExists(slug: String, category: String, page: Long): Boolean
    public fun deleteShowsByGenreSlugAndCategory(slug: String, category: String)
    public fun deleteShowsByGenreSlugCategoryAndPage(slug: String, category: String, page: Long)
    public fun deleteShowsByGenreSlug(slug: String)
}
