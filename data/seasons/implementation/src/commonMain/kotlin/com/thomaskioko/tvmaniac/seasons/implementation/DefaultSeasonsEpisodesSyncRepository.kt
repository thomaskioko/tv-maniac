package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.seasons.api.SeasonsEpisodesSyncRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSeasonsEpisodesSyncRepository(
    private val store: SeasonsWithEpisodesStore,
) : SeasonsEpisodesSyncRepository {

    override suspend fun syncSeasonsWithEpisodes(showTraktId: Long, forceRefresh: Boolean) {
        when {
            forceRefresh -> store.fresh(showTraktId)
            else -> store.get(showTraktId)
        }
    }
}
