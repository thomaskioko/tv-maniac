package com.thomaskioko.tvmaniac.data.rewatch.api

/**
 * How far a session has got through the show it belongs to, counting aired regular episodes only.
 *
 * [coversShow] follows Simkl's own rule for promoting a session to completed, so the local state
 * and the provider's agree instead of drifting apart. A show with nothing aired covers nothing,
 * which keeps a session on an unaired show from closing the moment it opens.
 */
public data class RewatchCoverage(
    val watchedInSession: Long,
    val airedTotal: Long,
) {
    public val coversShow: Boolean
        get() = airedTotal > 0 && watchedInSession >= airedTotal
}
