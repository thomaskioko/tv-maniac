package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository

class FakeEpisodeRepository : EpisodeRepository {

    override suspend fun updateEpisodeArtWork(showId: Long) {}
}