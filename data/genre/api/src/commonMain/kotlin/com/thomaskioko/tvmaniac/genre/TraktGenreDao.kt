package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.genre.model.TraktGenreEntity
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface TraktGenreDao {
    public fun upsertGenre(slug: String, name: String)
    public fun observeGenres(): Flow<List<TraktGenreEntity>>
    public fun getGenreSlugs(): List<String>
    public fun deleteAllGenres()
    public fun upsertGenreShow(genreSlug: String, traktId: Long, pageOrder: Long, category: String)
    public fun observeShowsByGenreSlug(slug: String): Flow<List<ShowEntity>>
    public fun observeShowsByGenreSlugAndCategory(slug: String, category: String): Flow<List<ShowEntity>>
    public fun observeGenresWithShowsByCategory(category: String): Flow<List<GenreWithShowsEntity>>
    public fun deleteShowsByGenreSlugAndCategory(slug: String, category: String)
    public fun deleteShowsByGenreSlug(slug: String)
}
