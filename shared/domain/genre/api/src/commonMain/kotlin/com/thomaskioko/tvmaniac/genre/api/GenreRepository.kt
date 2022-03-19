package com.thomaskioko.tvmaniac.genre.api

import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.datasource.cache.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {

    fun observeGenres(): Flow<Resource<List<Genre>>>
}
