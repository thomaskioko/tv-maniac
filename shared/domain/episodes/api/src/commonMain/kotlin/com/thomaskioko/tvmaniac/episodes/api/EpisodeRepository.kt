package com.thomaskioko.tvmaniac.episodes.api


interface EpisodeRepository {

    suspend fun updateEpisodeArtWork(showId: Int)
}
