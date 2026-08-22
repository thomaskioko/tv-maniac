package com.thomaskioko.tvmaniac.shows.testing

import com.thomaskioko.tvmaniac.shows.api.ShowTraktIdResolver

public class FakeShowTraktIdResolver : ShowTraktIdResolver {

    private var resolvedCount: Int = 0
    private var failure: Throwable? = null
    private val requestedIds = mutableListOf<List<Long>>()

    public fun setResolvedCount(count: Int) {
        resolvedCount = count
    }

    public fun setFailure(error: Throwable) {
        failure = error
    }

    public fun requestedIds(): List<List<Long>> = requestedIds

    override suspend fun resolveMissingTraktIds(tmdbIds: List<Long>): Int {
        requestedIds.add(tmdbIds)
        failure?.let { throw it }
        return resolvedCount
    }
}
