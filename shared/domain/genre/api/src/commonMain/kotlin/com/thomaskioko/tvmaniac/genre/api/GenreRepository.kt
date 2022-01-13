package com.thomaskioko.tvmaniac.genre.api

import com.thomaskioko.tvmaniac.datasource.cache.Genre
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface GenreRepository {

    fun observeGenres(): Flow<Resource<List<Genre>>>
}
