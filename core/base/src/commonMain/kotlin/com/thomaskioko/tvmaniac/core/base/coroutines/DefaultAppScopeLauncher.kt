package com.thomaskioko.tvmaniac.core.base.coroutines

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultAppScopeLauncher(
    @IoCoroutineScope private val appCoroutineScope: CoroutineScope,
    private val logger: Logger,
) : AppScopeLauncher {

    override fun launch(
        tag: String,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = appCoroutineScope.launch {
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            logger.error(tag, "Background job failed", throwable)
        }
    }
}
