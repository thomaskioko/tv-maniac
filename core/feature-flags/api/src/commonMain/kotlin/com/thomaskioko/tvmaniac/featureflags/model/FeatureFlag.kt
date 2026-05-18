package com.thomaskioko.tvmaniac.featureflags.model

import kotlinx.datetime.LocalDate

/**
 * Catalog of feature flags consumed across the app. Each entry binds a stable Firebase Remote
 * Config [key] to its in-app [defaultValue] plus debug-screen metadata.
 *
 * @property key Remote Config key string. Must match the Firebase console entry exactly.
 * @property defaultValue Value used when Remote Config is unavailable or has not fetched yet.
 * @property title Human-readable name shown on the debug screen row.
 * @property description One-line summary shown beneath the title on the debug screen.
 * @property dateAdded Date the flag entered the codebase. Drives the "Date Added" sort.
 */
public enum class FeatureFlag(
    public val key: String,
    public val defaultValue: Boolean,
    public val title: String,
    public val description: String,
    public val dateAdded: LocalDate,
) {
    SIMKL_LOGIN_ENABLED(
        key = "simkl_login_enabled",
        defaultValue = false,
        title = "Simkl Login",
        description = "Show the Simkl login entry point on the settings screen.",
        dateAdded = LocalDate(2026, 5, 17),
    ),
}
