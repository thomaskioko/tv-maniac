package com.thomaskioko.tvmaniac.shows.testing

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse

class FakeShowsRepository : ShowsRepository {

    private var featuredResult = flowOf<StoreReadResponse<List<ShowsByCategory>>>()

    private var anticipatedResult = flowOf<StoreReadResponse<List<ShowsByCategory>>>()

    private var popularResult = flowOf<StoreReadResponse<List<ShowsByCategory>>>()

    private var trendingResult = flowOf<StoreReadResponse<List<ShowsByCategory>>>()

    private var showResult = flowOf<StoreReadResponse<ShowById>>()

    suspend fun setFeaturedResult(result: StoreReadResponse<List<ShowsByCategory>>) {
        featuredResult = flow { emit(result) }
    }

    suspend fun setAnticipatedResult(result: StoreReadResponse<List<ShowsByCategory>>) {
        anticipatedResult = flow { emit(result) }
    }

    suspend fun setPopularResult(result: StoreReadResponse<List<ShowsByCategory>>) {
        popularResult = flow { emit(result) }
    }

    suspend fun setTrendingResult(result: StoreReadResponse<List<ShowsByCategory>>) {
        trendingResult = flow { emit(result) }
    }

    suspend fun setShowResult(result: StoreReadResponse<ShowById>) {
        showResult = flow { emit(result) }
    }

    override fun observeShow(traktId: Long): Flow<StoreReadResponse<ShowById>> = showResult

    override fun observeShows(categoryId: Long): Flow<StoreReadResponse<List<ShowsByCategory>>> {
        TODO("Not yet implemented")
    }

    override fun fetchTrendingShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        trendingResult

    override fun fetchPopularShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        popularResult

    override fun fetchAnticipatedShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        anticipatedResult

    override fun fetchFeaturedShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        featuredResult

    override suspend fun fetchShows() {}

    override suspend fun fetchShows(category: Category): List<ShowsByCategory> = emptyList()

    override suspend fun fetchShow(traktId: Long): ShowById = selectedShow
}

val selectedShow = ShowById(
    trakt_id = 84958,
    tmdb_id = 849583,
    title = "Loki",
    overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
        "an alternate version of Loki is brought to the mysterious Time Variance " +
        "Authority, a bureaucratic organization that exists outside of time and " +
        "space and monitors the timeline. They give Loki a choice: face being " +
        "erased from existence due to being a “time variant”or help fix " +
        "the timeline and stop a greater threat.",
    language = "en",
    votes = 4958,
    rating = 8.1,
    genres = listOf("Horror", "Action"),
    status = "Returning Series",
    year = "2024",
    runtime = 45,
    poster_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdrop_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    aired_episodes = 12,
    trakt_id_ = 1234,
    id = 12345,
    created_at = null,
    synced = false,
    tmdb_id_ = 1232,
)
