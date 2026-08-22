package com.thomaskioko.tvmaniac.shows.api

public interface ShowTraktIdResolver {
    public suspend fun resolveMissingTraktIds(tmdbIds: List<Long>): Int
}
