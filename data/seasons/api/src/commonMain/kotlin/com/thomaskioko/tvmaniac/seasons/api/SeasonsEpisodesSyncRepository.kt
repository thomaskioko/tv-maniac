package com.thomaskioko.tvmaniac.seasons.api

public interface SeasonsEpisodesSyncRepository {
    public suspend fun syncSeasonsWithEpisodes(showId: Long, forceRefresh: Boolean = false)
}
