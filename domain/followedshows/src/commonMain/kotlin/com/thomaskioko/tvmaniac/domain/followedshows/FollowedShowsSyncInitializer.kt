package com.thomaskioko.tvmaniac.domain.followedshows

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class FollowedShowsSyncInitializer(
    private val traktAuthRepository: TraktAuthRepository,
    private val followedShowsRepository: FollowedShowsRepository,
    private val showDetailsInteractor: ShowDetailsInteractor,
    private val coroutineScope: AppCoroutineScope,
    private val logger: Logger,
) : AppInitializer {

    override fun init() {
        coroutineScope.io.launch {
            traktAuthRepository.state
                .distinctUntilChanged()
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect {
                    logger.debug(TAG, "Auth state changed to LOGGED_IN, syncing followed shows...")
                    try {
                        followedShowsRepository.syncFollowedShows()
                        fetchShowDetailsForFollowedShows()
                    } catch (e: Exception) {
                        logger.error(TAG, "Failed to sync followed shows: ${e.message}")
                    }
                }
        }
    }

    private suspend fun fetchShowDetailsForFollowedShows() {
        val followedShows = followedShowsRepository.observeFollowedShows().first()
        logger.debug(TAG, "Fetching details for ${followedShows.size} followed shows...")

        followedShows.forEach { show ->
            try {
                showDetailsInteractor.executeSync(
                    ShowDetailsInteractor.Param(id = show.tmdbId, forceRefresh = false),
                )
            } catch (e: Exception) {
                logger.error(TAG, "Failed to fetch details for show ${show.tmdbId}: ${e.message}")
            }
        }
    }

    private companion object {
        private const val TAG = "FollowedShowsSyncInitializer"
    }
}
