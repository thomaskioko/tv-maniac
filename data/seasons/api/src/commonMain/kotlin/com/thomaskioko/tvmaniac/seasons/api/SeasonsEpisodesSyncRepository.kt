package com.thomaskioko.tvmaniac.seasons.api

public interface SeasonsEpisodesSyncRepository {
    public suspend fun syncSeasonsWithEpisodes(showTraktId: Long, forceRefresh: Boolean = false)
}
