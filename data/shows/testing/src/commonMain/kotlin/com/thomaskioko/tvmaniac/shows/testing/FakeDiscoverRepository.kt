package com.thomaskioko.tvmaniac.shows.testing

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.time.Duration

class FakeDiscoverRepository : DiscoverRepository {

    private var showById: Channel<ShowById> = Channel(Channel.UNLIMITED)
    private var updatedShowCategoryResult: Channel<Either<Failure, List<ShowsByCategory>>> =
        Channel(Channel.UNLIMITED)

    private var showCategoryResult: Channel<List<ShowsByCategory>> = Channel(Channel.UNLIMITED)

    private var showByIdResult: Channel<Either<Failure, ShowById>> =
        Channel(Channel.UNLIMITED)

    suspend fun setShowCategory(result: List<ShowsByCategory>) {
        showCategoryResult.send(result)
    }

    suspend fun setShowById(result: ShowById) {
        showById.send(result)
    }

    suspend fun setTrendingResult(result: Either<Failure, List<ShowsByCategory>>) {
        updatedShowCategoryResult.send(result)
    }

    suspend fun setShowResult(result: Either<Failure, ShowById>) {
        showByIdResult.send(result)
    }

    override fun observeShow(traktId: Long): Flow<Either<Failure, ShowById>> =
        showByIdResult.receiveAsFlow()

    override fun observeShowCategory(
        category: Category,
        duration: Duration,
    ): Flow<Either<Failure, List<ShowsByCategory>>> = updatedShowCategoryResult.receiveAsFlow()

    override suspend fun fetchDiscoverShows() {}

    override suspend fun fetchShows(category: Category): List<ShowsByCategory> =
        showCategoryResult.receive()

    override suspend fun getShowById(traktId: Long): ShowById = showById.receive()
}

val selectedShow = ShowById(
    id = Id(84958),
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
    in_watchlist = 0,
)
