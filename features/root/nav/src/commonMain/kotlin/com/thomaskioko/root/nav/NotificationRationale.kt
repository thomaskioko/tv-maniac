package com.thomaskioko.root.nav

public interface NotificationRationale {
    /**
     * Shows the notification permission rationale on the next idle tick if the user has not yet
     * been asked and no rationale is currently showing. Safe to call repeatedly; a no-op when the
     * preconditions are not met.
     */
    public suspend fun showIfNeeded()
}
