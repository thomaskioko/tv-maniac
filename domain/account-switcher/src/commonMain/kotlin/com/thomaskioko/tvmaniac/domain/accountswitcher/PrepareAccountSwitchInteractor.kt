package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.core.logger.Logger
import dev.zacsweers.metro.Inject

@Inject
public class PrepareAccountSwitchInteractor(
    private val pushPendingChangesInteractor: PushPendingChangesInteractor,
    private val countUnsavedChanges: CountUnsavedChanges,
    private val logger: Logger,
) {

    public suspend operator fun invoke(): Int {
        runCatching { pushPendingChangesInteractor.executeSync() }
            .onFailure { logger.warning(TAG, "Pushing pending changes before switch failed: ${it.message}") }

        return runCatching { countUnsavedChanges() }
            .onFailure { logger.warning(TAG, "Counting unsaved changes before switch failed: ${it.message}") }
            .getOrDefault(0)
    }

    private companion object {
        private const val TAG = "PrepareAccountSwitch"
    }
}
