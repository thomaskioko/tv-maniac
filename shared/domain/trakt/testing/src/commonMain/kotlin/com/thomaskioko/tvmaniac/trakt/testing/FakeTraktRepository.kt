package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakeTraktRepository : TraktRepository {

    override fun observeMe(slug: String): Flow<Resource<Trakt_user>> = flowOf(
        Resource.Success(
            Trakt_user(
                slug = "me",
                user_name = "silly_eyes",
                full_name = "Stranger Danger",
                profile_picture = "",
                is_me = true
            )
        )
    )

    override fun observeStats(slug: String, refresh: Boolean): Flow<Resource<TraktStats>> =
        flowOf(
            Resource.Success(
                TraktStats(
                    user_slug = "me",
                    months = "148",
                    days = "54",
                    hours = "142",
                    collected_shows = "1200",
                    episodes_watched = "8200"
                )
            )
        )

    override fun observeCreateTraktList(userSlug: String): Flow<Resource<Trakt_list>> =
        flowOf(
            Resource.Success(
                Trakt_list(
                    id = 45,
                    slug = "favorites",
                    description = "Favorite Shows"
                )
            )
        )

    override fun observeFollowedShows(): Flow<List<SelectFollowedShows>> =
        flowOf(
            listOf(
                SelectFollowedShows(
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
                    id = 12,
                    synced = true,
                    created_at = 12345645,
                    trakt_id_ = 1232
                )
            )
        )

    override fun observeUpdateFollowedShow(
        traktId: Int,
        addToWatchList: Boolean
    ): Flow<Resource<Unit>> = flowOf(Resource.Success(Unit))

    override fun observeShow(traktId: Int): Flow<Resource<SelectByShowId>> =
        flowOf(
            Resource.Success(
                SelectByShowId(
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
                    synced = false
                )
            )
        )

    override fun fetchShowsByCategoryId(categoryId: Int): Flow<Resource<List<SelectShowsByCategory>>> =
        flowOf(
            Resource.Success(
                listOf(
                    SelectShowsByCategory(
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
                        category_id = 1,
                        trakt_id__ = 12345
                    )
                )
            )
        )

    override fun observeCachedShows(categoryId: Int): Flow<List<SelectShowsByCategory>> =
        flowOf(
            listOf(
                SelectShowsByCategory(
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
                    category_id = 1,
                    trakt_id__ = 12345
                )
            )
        )


    override suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean) {}

    override suspend fun fetchTraktWatchlistShows() {}

    override suspend fun fetchShows() {}

    override suspend fun syncFollowedShows() {}

}
