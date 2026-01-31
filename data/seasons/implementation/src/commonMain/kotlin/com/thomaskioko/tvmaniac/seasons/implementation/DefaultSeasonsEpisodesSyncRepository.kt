package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.seasons.api.SeasonsEpisodesSyncRepository
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
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
