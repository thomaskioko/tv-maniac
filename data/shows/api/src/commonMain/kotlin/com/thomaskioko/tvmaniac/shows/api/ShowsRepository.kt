package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.StoreReadResponse

interface ShowsRepository {

    fun observeShow(traktId: Long): Flow<StoreReadResponse<ShowById>>

    fun observeRecommendedShows(categoryId: Long): Flow<StoreReadResponse<List<ShowsByCategory>>>

    fun observeTrendingShows(): Flow<StoreReadResponse<List<ShowsByCategory>>>

    fun observePopularShows(): Flow<StoreReadResponse<List<ShowsByCategory>>>

    fun observeAnticipatedShows(): Flow<StoreReadResponse<List<ShowsByCategory>>>

    fun observeFeaturedShows(): Flow<StoreReadResponse<List<ShowsByCategory>>>

    suspend fun fetchDiscoverShows()

    suspend fun fetchShow(traktId: Long): ShowById

    suspend fun fetchShows(category: Category): List<ShowsByCategory>
}
