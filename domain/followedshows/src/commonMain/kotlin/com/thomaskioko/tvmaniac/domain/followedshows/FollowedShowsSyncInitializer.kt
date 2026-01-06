package com.thomaskioko.tvmaniac.domain.followedshows

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.followedshows.FollowedShowsSyncInteractor.Param
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class FollowedShowsSyncInitializer(
    private val traktAuthRepository: TraktAuthRepository,
    private val followedShowsSyncInteractor: FollowedShowsSyncInteractor,
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
                    followedShowsSyncInteractor.executeSync(Param())
                }
        }
    }

    private companion object {
        private const val TAG = "FollowedShowsSyncInitializer"
    }
}
