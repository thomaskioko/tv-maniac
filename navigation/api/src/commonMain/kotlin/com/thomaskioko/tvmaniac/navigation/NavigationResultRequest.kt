package com.thomaskioko.tvmaniac.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/**
 * Represents a pending request for a result from another destination.
 *
 * Create one by injecting [NavigationResultRegistry] and calling
 * [registerForNavigationResult] from the source screen. Pass [key] into the target route so that
 * the target can match deliveries back to this request.
 */
public class NavigationResultRequest<R : Any> @PublishedApi internal constructor(
    public val key: Key<R>,
    public val results: Flow<R>,
) {
    /**
     * Identifier for a pending navigation-result. The key is [Serializable] so it can be embedded
     * inside a [NavRoute] and round-trip through save/restore.
     */
    @Serializable
    public data class Key<R : Any>(
        internal val ownerRouteQualifiedName: String,
        internal val resultQualifiedName: String,
    )
}
