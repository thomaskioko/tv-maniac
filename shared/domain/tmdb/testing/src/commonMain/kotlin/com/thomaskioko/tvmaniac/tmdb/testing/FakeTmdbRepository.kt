package com.thomaskioko.tvmaniac.tmdb.testing

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakeTmdbRepository : TmdbRepository {
    override fun observeShow(tmdbId: Int): Flow<Resource<SelectByShowId>> =
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
                    trakt_id_ = null,
                    id = 84958,
                    synced = false,
                    created_at = null
                )
            )
        )

    override fun updateShowArtWork(): Flow<Resource<Unit>> = flowOf(Resource.Success(Unit))

}