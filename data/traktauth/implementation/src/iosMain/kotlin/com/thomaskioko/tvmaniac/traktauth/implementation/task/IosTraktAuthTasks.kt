package com.thomaskioko.tvmaniac.traktauth.implementation.task

import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTask
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskRegistry
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthTasks
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = TraktAuthTasks::class)
@ContributesBinding(AppScope::class, boundType = BackgroundTask::class, multibinding = true)
public class IosTraktAuthTasks(
    private val registry: BackgroundTaskRegistry,
    private val traktAuthRepository: TraktAuthRepository,
) : TraktAuthTasks, BackgroundTask {

    override val taskId: String = TASK_ID
    override val interval: Double = REFRESH_INTERVAL_SECONDS

    override fun setup() {
        registry.register(this)
    }

    override fun scheduleTokenRefresh() {
        registry.schedule(taskId)
    }

    override fun cancelTokenRefresh() {
        registry.cancel(taskId)
    }

    override suspend fun execute() {
        val authState = traktAuthRepository.getAuthState() ?: return
        if (!authState.isExpiringSoon()) return
        traktAuthRepository.refreshTokens()
    }

    private companion object {
        private const val TASK_ID = "com.thomaskioko.tvmaniac.tokenrefresh"
        private const val REFRESH_INTERVAL_SECONDS = 5.0 * 24.0 * 60.0 * 60.0
    }
}
