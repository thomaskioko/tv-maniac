package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class TokenRefreshInitializer(
    private val tasks: TraktAuthTasks,
    private val traktAuthRepository: TraktAuthRepository,
    dispatchers: AppCoroutineDispatchers,
) : AppInitializer {
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)

    override fun init() {
        tasks.setup()

        scope.launch {
            traktAuthRepository.state.collectLatest { state ->
                when (state) {
                    TraktAuthState.LOGGED_IN -> tasks.scheduleTokenRefresh()
                    TraktAuthState.LOGGED_OUT -> tasks.cancelTokenRefresh()
                }
            }
        }
    }
}
